package com.ljc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> { // 必须有这个 <T>
    private Integer code;
    private String message;
    private T data;

    // 成功返回的静态方法
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 失败返回的静态方法
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}