package fun.sast.service.impl;

import com.wf.captcha.SpecCaptcha;
import fun.sast.entity.VerifyCode;
import fun.sast.service.LoginService;
import fun.sast.utils.RedisUtil;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    private final RedisUtil redisUtil;

    public LoginServiceImpl(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public VerifyCode getVerifyCode() {
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 5); // 宽度, 高度, 字符数
        String verifyCodeText = captcha.text();
        String verifyCodeKey = UUID.randomUUID().toString();
        // 验证码图片base64
        VerifyCode verifyCode = new VerifyCode(verifyCodeKey, captcha.toBase64(), verifyCodeText);

        // 将验证码文本存入Redis，过期时间60s
        redisUtil.set(verifyCodeKey, verifyCodeText, 60);
        System.out.println(redisUtil.get(verifyCodeKey));

        return verifyCode;
    }
}
