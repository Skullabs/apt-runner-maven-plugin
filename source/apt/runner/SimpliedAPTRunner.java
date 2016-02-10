package apt.runner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class SimpliedAPTRunner {

	final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	final List<String> compilerOptions = Arrays.asList( "-proc:only" );

	final JavaCompiler compiler;
	final StandardJavaFileManager fileManager;
	final Config config;

	public SimpliedAPTRunner() {
		this( new Config(), ToolProvider.getSystemJavaCompiler() );
	}

	public SimpliedAPTRunner( Config config, JavaCompiler compiler ) {
		this.config = config;
		this.compiler = compiler;
		this.fileManager = createFileManager();
	}

	public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
		return diagnostics.getDiagnostics();
	}

	public APTResult run( JavaFileObject... compilationUnits ) {
		final List<JavaFileObject> compilationUnitsAsList = Arrays.asList( compilationUnits );
		return run( compilationUnitsAsList );
	}

	public APTResult run( Iterable<? extends JavaFileObject> compilationUnits ) {
		final CompilationTask task = compiler.getTask( null, fileManager, diagnostics, compilerOptions, null, compilationUnits );
		final boolean success = task.call();
		final List<Diagnostic<? extends JavaFileObject>> generatedDiagnostics = diagnostics.getDiagnostics();
		return new APTResult( success, generatedDiagnostics );
	}

	public Iterable<? extends JavaFileObject> readFile( File... files ) {
		return fileManager.getJavaFileObjects( files );
	}

	public JavaFileObject readClass( String className ) {
		try {
			return fileManager.getJavaFileForInput( StandardLocation.CLASS_OUTPUT, className, Kind.CLASS );
		} catch ( final IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	private StandardJavaFileManager createFileManager() {
		try {
			ensureThatConfigDirectoriesExists( config );
			final StandardJavaFileManager fileManager = compiler.getStandardFileManager( diagnostics, null, null );
			if ( config.classPath != null )
				fileManager.setLocation( StandardLocation.CLASS_PATH, config.classPath );
			fileManager.setLocation( StandardLocation.CLASS_OUTPUT, config.classOutputDir );
			fileManager.setLocation( StandardLocation.SOURCE_PATH, config.sourceDir );
			fileManager.setLocation( StandardLocation.SOURCE_OUTPUT, config.outputDir );
			return fileManager;
		} catch ( final IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	static void ensureThatConfigDirectoriesExists( Config config ) {
		ensureThatConfigDirectoryExists( config.sourceDir, false );
		ensureThatConfigDirectoryExists( config.outputDir, true );
		ensureThatConfigDirectoryExists( config.classOutputDir, true );
	}

	static void ensureThatConfigDirectoryExists( List<File> dirs, boolean forceCreate ) {
		for ( final File dir : dirs )
			ensureThatConfigDirectoryExists( dir, forceCreate );
	}

	static void ensureThatConfigDirectoryExists( File dir, boolean forceCreate ) {
		if ( !dir.exists() )
			if ( !forceCreate || !dir.mkdirs() )
				throw new IllegalStateException( "Directory does not exists: " + dir );
	}
}
