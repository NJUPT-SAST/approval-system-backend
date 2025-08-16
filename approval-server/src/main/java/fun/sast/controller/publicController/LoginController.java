package fun.sast.controller.publicController;

import fun.sast.annotation.ResponseResult;
import fun.sast.dto.UserLoginDTO;
import fun.sast.dto.VerifyCodeDTO;
import fun.sast.response.GlobalResponse;
import fun.sast.service.LoginService;
import fun.sast.service.UserService;
import fun.sast.vo.UserLoginVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    /**
     * @return 获取验证码图片，头部以及响应体
     */
    @ResponseResult
    @GetMapping("/getValidateCode")
    public ResponseEntity<GlobalResponse<String>> getValidateCode(HttpServletResponse response) {
        // 获取验证码信息
        VerifyCodeDTO verifyCodeDTO = loginService.getVerifyCode();

        // 设置响应头
        response.setHeader("CAPTCHA", verifyCodeDTO.getKey());

        // 返回Base64图片数据
        return ResponseEntity.ok(GlobalResponse.success(verifyCodeDTO.getImage()));
    }
}
