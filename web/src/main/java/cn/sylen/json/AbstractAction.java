package cn.sylen.json;

import cn.sylen.common.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAction {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public <T> Result<T> buildSuccessResult(T data) {
        return new Result<T>().setSuccess(true).setCode("200").setData(data);
    }

    public <T> Result<T> buildSuccessResult(String message, T data) {
        return new Result<T>().setSuccess(true).setCode("200").setMsg(message).setData(data);
    }

    public Result<Object> buildFailuerResult(String message) {
        return new Result<Object>().setSuccess(false).setCode("500").setMsg(message);
    }

    public <T> Result<T> buildFailuerResult(String message, T data) {
        return new Result<T>().setSuccess(false).setCode("500").setMsg(message).setData(data);
    }

    public <T> Result<T> success(String message, T data) {
        return new Result<T>().setSuccess(true).setCode("200").setMsg(message).setData(data);
    }

    public <T> Result<T> error(String message, T data) {
        return new Result<T>().setSuccess(false).setCode("500").setMsg(message).setData(data);
    }

    public <T> Result<T> error(Exception e, T data) {
        return new Result<T>().setSuccess(false).setCode("500").setMsg(e.getMessage()).setData(data);
    }
}
