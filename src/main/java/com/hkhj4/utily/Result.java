package com.hkhj4.utily;
import lombok.Data;

@Data
public class Result {
    int code;
    String msg;
    Object data;

    public static Result result(int code, String msg, Object data) {
        Result result = new Result();
        result.code = code;
        result.msg = msg;
        result.data = data;
        return result;
    }

    public static Result success() {
        return result(200, "success", null);
    }
    public static Result success(Object data) {
        return Result.result(200, "success", data);
    }
    public static Result success(Integer code, String msg) {
        return result(code, msg, null);
    }

    public static Result error(int code, String msg) {
        return Result.result(code, msg, null);
    }
}
