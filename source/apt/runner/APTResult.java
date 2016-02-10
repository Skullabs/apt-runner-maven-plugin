package apt.runner;

import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

final public class APTResult {

	final boolean success;
	final List<Diagnostic<? extends JavaFileObject>> diagnostics;

	public APTResult( boolean success, List<Diagnostic<? extends JavaFileObject>> diagnostics ) {
		this.success = success;
		this.diagnostics = diagnostics;
	}

	public boolean isSuccess() {
		return success;
	}

	public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
		return diagnostics;
	}
}
