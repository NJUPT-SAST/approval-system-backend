package fun.sast.handler;

import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import fun.sast.response.GlobalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public GlobalResponse<ErrorEnum> exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return GlobalResponse.failure(ex.getErrorEnum());
    }

    @ExceptionHandler(Exception.class)
    public GlobalResponse<ErrorEnum> handleOtherException(Exception ex) {
        log.error("未知异常：", ex);
        return GlobalResponse.failure(ErrorEnum.COMMON_ERROR);
    }
}
