package fun.sast.controller.publicController;

import fun.sast.annotation.ResponseResult;
import fun.sast.dto.UserLoginDTO;
import fun.sast.dto.VerifyCodeDTO;
import fun.sast.service.LoginService;
import fun.sast.service.UserService;
import fun.sast.vo.UserLoginVO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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
    public UserLoginVO login(UserLoginDTO userLoginDTO, @RequestHeader String captcha)
            throws BadRequestException {
        UserLoginVO userLoginVO = userService.login(userLoginDTO, captcha);
        return userLoginVO;
    }

    @GetMapping("/getValidateCode")
    public void getValidateCode(HttpServletResponse response) throws IOException {
        VerifyCodeDTO verifyCodeDTO = loginService.getVerifyCode();
        // 原始 Base64 图片字符串
        String base64Image = verifyCodeDTO.getImage();
        // 去掉前缀 data:image/png;base64, 只保留纯 Base64 数据
        base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
        // 解码成字节数组
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        // 设置响应头
        response.setContentType("image/png");
        response.addHeader("CAPTCHA", verifyCodeDTO.getKey());
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        // 写入输出流
        response.getOutputStream().write(imageBytes);
        response.getOutputStream().flush();
    }
}
