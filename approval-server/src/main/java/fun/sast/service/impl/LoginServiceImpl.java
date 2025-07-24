package fun.sast.service.impl;

import com.wf.captcha.SpecCaptcha;
import fun.sast.entity.VerifyCode;
import fun.sast.service.LoginService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Override
    public VerifyCode getVerifyCode() {
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 5); // 宽度, 高度, 字符数
        String verifyCodeText = captcha.text();
        String verifyCodeKey = UUID.randomUUID().toString();
        // 验证码图片base64
        VerifyCode verifyCode = new VerifyCode(verifyCodeKey, captcha.toBase64(), verifyCodeText);

        return verifyCode;
    }
}
