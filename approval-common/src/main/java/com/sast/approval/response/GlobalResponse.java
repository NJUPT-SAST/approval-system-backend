package com.sast.approval.response;

import com.sast.approval.enums.ErrorEnum;
import lombok.Data;
import java.io.Serializable;

/**
 * 统一返回结果类
 * @param <T>
 */

@Data
public class GlobalResponse<T> implements Serializable {
    private Integer errCode;
    private String errMsg;
    private boolean success;
    private T data;

    public static <T> GlobalResponse<T> success(T data) {
        GlobalResponse<T> response = new GlobalResponse<>();
        response.setErrCode(null);
        response.setErrMsg(null);
        response.setData(data);
        return response;
    }

    public static <T> GlobalResponse<T> success() {
        return success(null);
    }

    public static <T> GlobalResponse<T> failure(ErrorEnum errorEnum) {
        GlobalResponse<T> response = new GlobalResponse<>();
        response.setErrCode(errorEnum.getErrCode());
        response.setErrMsg(errorEnum.getErrMsg());
        response.setSuccess(false);
        response.setData(null);
        return response;
    }


}
