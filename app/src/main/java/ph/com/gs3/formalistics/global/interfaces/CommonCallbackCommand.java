package ph.com.gs3.formalistics.global.interfaces;

public abstract class CommonCallbackCommand {
	public abstract void execute(Object... params);

	public void onError(Exception e) {
		// Default on error
		e.printStackTrace();
	}

	public void onError(Exception e, Object relatedObject) {
		// Default on error
		e.printStackTrace();
	}
}
