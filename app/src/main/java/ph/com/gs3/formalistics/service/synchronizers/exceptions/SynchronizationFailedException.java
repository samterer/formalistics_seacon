package ph.com.gs3.formalistics.service.synchronizers.exceptions;

public class SynchronizationFailedException extends Exception {

	private static final long serialVersionUID = 1904726646743494241L;

	public SynchronizationFailedException(String message) {
		super(message);
	}

	public SynchronizationFailedException(Throwable throwable) {
		super(throwable);
	}
	
	public SynchronizationFailedException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
