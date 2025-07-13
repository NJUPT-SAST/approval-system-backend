package fun.sast.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {
    /**
     * 登录验证码
     */
    private String validateCode;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 学号
     */
    private String code;
}
