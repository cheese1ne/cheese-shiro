package com.cheese.shiro.common.domain;

import com.cheese.shiro.common.enums.Status;

import java.io.Serializable;

/**
 * 默认响应值包装类
 *
 * @author sobann
 */
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 描述
     */
    private String msg;
    /**
     * 结果集
     */
    private T data;

    public R() {
    }

    public R(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(T data) {
        this.code = Status.OK.getCode();
        this.msg = Status.OK.getMsg();
        this.data = data;
    }

    public R(Status code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

    public R(Status code, T data) {
        this.code = code.getCode();
        this.msg = code.getMsg();
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> R<T> ok(T data) {
        Status status = Status.OK;
        if (data instanceof Boolean && Boolean.FALSE.equals(data)) {
            status = Status.FAILED;
        }
        return restResult(data, status);
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, Status.FAILED.getCode(), msg);
    }

    private static <T> R<T> restResult(T data, Status status) {
        return restResult(data, status.getCode(), status.getMsg());
    }

    private static <T> R<T> restResult(T data, Integer code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }
}
