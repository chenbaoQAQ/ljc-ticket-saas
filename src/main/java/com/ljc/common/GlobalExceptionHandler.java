package com.ljc.common;

//全局异常处理类

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice//确认是作用在conteoller层
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)//业务异常
    public Result<Void> handleBiz(BizException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)//参数异常
    public Result<Void> handleIllegalArg(IllegalArgumentException e) {
        return Result.error(400, e.getMessage());
    }

    @ExceptionHandler(Exception.class)//兜底异常（防止没有预料到的情况）
    public Result<Void> handleOther(Exception e) {
        // 生产环境别把 e.getMessage() 直接暴露，这里先用通用提示
        return Result.error(500, "系统异常");
    }
}

