package fun.sast.service;

import fun.sast.dto.VerifyCodeDTO;

public interface LoginService {
    // 返回验证码图片，将uuid放置header，将验证码存入redis
    VerifyCodeDTO getVerifyCode();
}
