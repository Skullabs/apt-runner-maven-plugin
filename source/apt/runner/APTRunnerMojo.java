package apt.runner;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.tools.ToolProvider;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo( name = "force-run-apt",
		defaultPhase = LifecyclePhase.COMPILE,
		requiresDependencyResolution = ResolutionScope.COMPILE )
public class APTRunnerMojo extends AbstractMojo {

	/**
	 * The directory for compiled classes.
	 */
	@Parameter( defaultValue = "${project.build.outputDirectory}", required = true, readonly = false )
	public String compileClassesDirectory;

	/**
	 * The directory for compiled classes.
	 */
	@Parameter( defaultValue = "${project.build.directory}/generated-sources/annotations", required = true, readonly = false )
	public String generatedSourcesDirectory;

	/**
	 * Project classpath.
	 */
	@Parameter( defaultValue = "${project.compileClasspathElements}", readonly = true, required = true )
	private List<String> classpathElements;

	ClassFileReader decompiler;
	SimpliedAPTRunner runner;
	Config config;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		readConfiguration();
		configRunnerAndDecompiler();

		final List<File> javaClassFiles = readJavaClassFiles();
		for ( final File file : javaClassFiles ) {
			final StringJavaSource decompiled = decompiler.decompile( file );
			runner.run( decompiled );
		}
	}

	private void configRunnerAndDecompiler() {
		decompiler = new ClassFileReader( new File( compileClassesDirectory ) );
		runner = new SimpliedAPTRunner( config, ToolProvider.getSystemJavaCompiler() );
	}

	private List<File> readJavaClassFiles() throws MojoExecutionException {
		try {
			final Path rootDir = Paths.get( compileClassesDirectory );
			return Files.walk( rootDir )
					.filter( p -> p.toString().endsWith( ".class" ) )
					.unordered().map( p -> p.toFile() ).collect( Collectors.toList() );
		} catch ( final IOException e ) {
			throw new MojoExecutionException( "Can't read java classes", e );
		}
	}

	private void readConfiguration() {
		config = new Config();
		config.classPath = classpathElements.stream().map( s -> new File( s ) ).collect( Collectors.toList() );
		config.sourceDir = asList( new File( compileClassesDirectory ) );
		config.outputDir = asList( new File( generatedSourcesDirectory ) );
		config.classOutputDir = asList( new File( generatedSourcesDirectory ) );
	}
}
