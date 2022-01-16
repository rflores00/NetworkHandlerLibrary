package cl.rf.networkhandler.model;

import cl.rf.networkhandler.core.Status;

public class Resource<T> {

    private int code;
    private Status status;
    private T data;
    private String message;

    public Resource() {
    }

    public Resource(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public Resource<T> success(T data, int code){
        this.data = data;
        this.status = Status.SUCCESS;
        this.message = "";
        this.code = code;
        return this;
    }

    public Resource<T> error(Error error, T data){
        this.status = Status.ERROR;
        this.data = data;
        this.code = error.getCode();
        this.message = error.getMessage();

        return this;
    }

    public Resource<T> loading(T data){
        this.status = Status.LOADING;
        this.data = data;
        this.message = "";

        return this;
    }

    public Status status() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }
}
