package ph.com.gs3.formalistics.global.interfaces;

public abstract class CallbackCommand<T> {
	public abstract void execute(T result);

	public void onError(Exception e) {
		// Default on error
		e.printStackTrace();
	}

	public void onError(Exception e, Object relatedObject) {
		// Default on error
		e.printStackTrace();
	}

}
