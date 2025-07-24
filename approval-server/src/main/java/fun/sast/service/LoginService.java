package fun.sast.service;

import fun.sast.entity.VerifyCode;

public interface LoginService {
    VerifyCode getVerifyCode();
}
