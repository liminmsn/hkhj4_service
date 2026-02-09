package com.hkhj4.utils;

public class Result {
    public Integer code;
    public String msg;
    public Object data;

    Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result success() {
        return new Result(1, "请求成功", null);
    }

    public static Result success(Object data) {
        return new Result(1, "请求成功", data);
    }

    public static Result error(Object data) {
        return new Result(0, "请求失败", data);
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ",msg='" + msg + '\'' +
                ",data=" + data +
                "}";
    }
}
