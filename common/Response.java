package common;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 2L; // FIXED VALUE

    private boolean success;
    private String errorMessage;
    private Object data;

    public Response() {
    }

    public Response(boolean success, String errorMessage, Object data) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{success=" + success +
                ", error='" + errorMessage +
                "', data=" + data + "}";
    }
}