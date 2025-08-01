package fun.sast.service.impl;

import com.wf.captcha.SpecCaptcha;
import fun.sast.dto.VerifyCodeDTO;
import fun.sast.service.LoginService;
import fun.sast.utils.RedisUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final RedisUtil redisUtil;

    /**
     * @return 验证码的key,image,text,其中key与text存入redis，60s过期
     */
    @Override
    public VerifyCodeDTO getVerifyCode() {
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 5); // 宽度, 高度, 字符数
        String verifyCodeText = captcha.text();
        String verifyCodeKey = UUID.randomUUID().toString();
        // 验证码图片base64
        VerifyCodeDTO verifyCodeDTO =
                new VerifyCodeDTO(verifyCodeKey, captcha.toBase64(), verifyCodeText);
        // 将验证码文本存入Redis，过期时间60s
        redisUtil.set(verifyCodeKey, verifyCodeText, 60);
        System.out.println(redisUtil.get(verifyCodeKey));

        return verifyCodeDTO;
    }
}
