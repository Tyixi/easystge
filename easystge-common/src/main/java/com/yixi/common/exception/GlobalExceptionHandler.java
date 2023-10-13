package com.yixi.common.exception;

import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.ResultUtils;
import io.netty.util.internal.ThrowableUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException:"+e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(BusinessException e){
        log.error("RuntimeException:"+e.getMessage(), e);
        return ResultUtils.error(EventCode.SYSTEM_ERROR, e.getMessage(), "");
    }


    /**
     * 处理所有不可知的异常
     */
    @ExceptionHandler(Throwable.class)
    public BaseResponse handleException(Throwable e){
        log.error("Throwable:"+e.getMessage(), e);
        return ResultUtils.error(EventCode.SYSTEM_ERROR, e.getMessage(), "");
    }




}
