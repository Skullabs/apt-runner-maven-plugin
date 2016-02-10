package apt.runner;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.List;

public class Config {

	List<File> sourceDir = asList( file( "source" ), file( "src/main/java" ) );
	List<File> outputDir = asList( file( "target" ), file( "output" ) );
	List<File> classOutputDir = outputDir;
	List<File> classPath;

	private static File file( String path ) {
		return new File( path );
	}
}
