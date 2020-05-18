package gpse.umfrato.domain.security;

public class ApiResponse<T> {

    private int status;
    private String message;
    private Object result;

    public ApiResponse(final int status, final String message, final Object result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(final Object result) {
        this.result = result;
    }
}
