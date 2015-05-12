package ph.com.gs3.formalistics.service.synchronizers.exceptions;

import java.util.ArrayList;
import java.util.List;

public class SynchronizationPrematureException extends Exception {

	private static final long serialVersionUID = -189004889368403110L;

	private List<SynchronizationFailedException> syncFailures = new ArrayList<>();

	public SynchronizationPrematureException(String message) {
		super(message);
	}

	public SynchronizationPrematureException(Throwable throwable) {
		super(throwable);
	}

	public SynchronizationPrematureException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public SynchronizationPrematureException(List<SynchronizationFailedException> syncFailures) {
		super("There are items that were not synchronized properly");
		this.syncFailures = syncFailures;
	}

	public SynchronizationPrematureException(String message,
	        List<SynchronizationFailedException> syncFailures) {
		super(message);
		this.syncFailures = syncFailures;
	}

	public List<SynchronizationFailedException> getSyncFailures() {
		return syncFailures;
	}

}
