package core.support.logger;

import org.apache.log4j.Priority;

public class LogObject {

	public String value;
	public Priority priority;

	public LogObject() {
	}

	public LogObject(String value, Priority priority) {
		this.value = value;
		this.priority = priority;
	}
}