package apt.runner;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.junit.Before;
import org.junit.Test;

public class SimplifiedAPTRunnerTest {

	static final String SOURCE_DIR = "tests-resources";
	static final String OUTPUT_DIR = "output";

	final ClassFileReader reader = new ClassFileReader( new File( OUTPUT_DIR ) );

	SimpliedAPTRunner runner;

	@Before
	public void setup() {
		final Config config = new Config();
		config.sourceDir = asList( new File( SOURCE_DIR ) );
		config.outputDir = asList( new File( OUTPUT_DIR ) );
		config.classOutputDir = asList( new File( OUTPUT_DIR ) );
		runner = new SimpliedAPTRunner( config, ToolProvider.getSystemJavaCompiler() );
	}

	@Test
	public void example() throws IOException {
		final File compiledFile = new File( SOURCE_DIR, "apt/runner/User.class" );
		final StringJavaSource decompiled = reader.decompile( compiledFile );
		final APTResult result = runner.run( decompiled );
		printErrorsIfAny( result );
		assertTrue( result.success );

		final int size = result.diagnostics.size();
		assertTrue( size >= 3 && size < 5 );
	}

	private void printErrorsIfAny( final APTResult result ) {
		for ( final Diagnostic<? extends JavaFileObject> diagnostic : result.diagnostics )
			System.err.println( diagnostic );
	}
}