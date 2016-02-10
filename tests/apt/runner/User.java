package apt.runner;

import apt.runner.sample.Visit;

@Visit
public class User {

	@Visit
	String name;
	long id;

	@Visit
	public String getName() {
		return name;
	}
}
