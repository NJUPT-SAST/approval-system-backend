package fun.sast.controller.publicController;

import fun.sast.annotation.ResponseResult;
import fun.sast.dto.UserLoginDTO;
import fun.sast.dto.VerifyCodeDTO;
import fun.sast.service.LoginService;
import fun.sast.service.UserService;
import fun.sast.vo.UserLoginVO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final UserService userService;

    /**
     * 登录
     *
     * @param userLoginDTO 传入的账号密码验证码
     * @return UserLoginVO
     */
    @ResponseResult
    @PostMapping("/login")
    public UserLoginVO login(UserLoginDTO userLoginDTO, @RequestHeader String captcha) {
        return userService.login(userLoginDTO, captcha);
    }

    @GetMapping("/getValidateCode")
    public void getValidateCode(HttpServletResponse response) throws IOException {
        VerifyCodeDTO verifyCodeDTO = loginService.getVerifyCode();
        // 设置响应头
        response.setContentType("image/png");
        response.addHeader("CAPTCHA", verifyCodeDTO.getKey());
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        // 写入输出流
        response.getOutputStream().write(verifyCodeDTO.getImage());
        response.getOutputStream().flush();
    }
}
