package apt.runner.sample;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes( "apt.runner.*" )
public class VisitorProcessor extends AbstractProcessor {

	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {
		final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith( Visit.class );
		for ( final Element element : elements ) {
			final String elementAsString = element.asType().toString();
			processingEnv.getMessager().printMessage( Kind.MANDATORY_WARNING, "Added " + elementAsString );
		}
		return false;
	}

	/**
	 * We just return the latest version of whatever JDK we run on. Stupid?
	 * Yeah, but it's either that or warnings on all versions but 1. Blame Joe.
	 *
	 * PS: this method was copied from Project Lombok. ;)
	 */
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.values()[SourceVersion.values().length - 1];
	}
}
