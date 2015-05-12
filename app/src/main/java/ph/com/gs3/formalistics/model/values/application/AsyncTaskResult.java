package ph.com.gs3.formalistics.model.values.application;

/**
 * Created by Ervinne on 4/8/2015.
 */
public class AsyncTaskResult<T, E> {
    private T result;
    private E exception;

    private boolean isOperationSuccessful;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public E getException() {
        return exception;
    }

    public void setException(E exception) {
        this.exception = exception;
    }

    public boolean isOperationSuccessful() {
        return isOperationSuccessful;
    }

    public void setOperationSuccessful(boolean isOperationSuccessful) {
        this.isOperationSuccessful = isOperationSuccessful;
    }
}
