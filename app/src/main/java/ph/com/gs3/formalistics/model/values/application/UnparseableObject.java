package ph.com.gs3.formalistics.model.values.application;

public class UnparseableObject {
    private String formJSONString;
    private Exception exception;

    public UnparseableObject(String formJSONString, Exception exception) {
        this.formJSONString = formJSONString;
        this.exception = exception;
    }

    public String getFormJSON() {
        return formJSONString;
    }

    public void setFormJSON(String formJSON) {
        this.formJSONString = formJSONString;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}