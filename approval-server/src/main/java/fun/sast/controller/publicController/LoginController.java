package fun.sast.controller.publicController;

import fun.sast.Exception.BaseException;
import fun.sast.annotation.ResponseResult;
import fun.sast.dto.RegisterUserDTO;
import fun.sast.dto.UserLoginDTO;
import fun.sast.entity.Token;
import fun.sast.entity.VerifyCode;
import fun.sast.enums.ErrorEnum;
import fun.sast.response.GlobalResponse;
import fun.sast.service.LoginService;
import fun.sast.service.UserService;
import fun.sast.utils.RedisUtil;
import java.util.Optional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    //    @Autowired
    // private UserService;
    @Autowired private LoginService loginService;
    @Autowired private UserService userService;
    @Autowired private RedisUtil redisUtil;

    /**
     * 登录
     *
     * @param userLoginDTO
     * @return UserLoginVO
     */
    @PostMapping("/login")
    @ResponseResult
    public GlobalResponse login(UserLoginDTO userLoginDTO, @RequestHeader String captcha)
            throws BadRequestException {
        // 验证验证码
        String currentCode = (String) redisUtil.get(captcha);
        if (currentCode == null) {
            throw new BaseException(ErrorEnum.INVALID_CAPTCHA);
        } else if (!currentCode.equals(userLoginDTO.getValidateCode())) {
            throw new BaseException(ErrorEnum.INCORRECT_CAPTCHA);
        }
        // 删除redis中的验证码
        redisUtil.delete(captcha);
        if ("".equals(userLoginDTO.getCode()) || "".equals(userLoginDTO.getValidateCode())) {
            throw new BaseException(ErrorEnum.USERNAME_OR_PASSWORD_EMPTY);
        }
        // 登录逻辑
        Token token = new Token();
        token.setToken(userService.login(userLoginDTO));
        return Optional.ofNullable(userService.login(userLoginDTO))
                .map(user -> GlobalResponse.success(token))
                .orElse(GlobalResponse.failure(ErrorEnum.LOGIN_ERROR));

        // 从数据库查询

    }

    @GetMapping("/getValidateCode")
    public ResponseEntity<GlobalResponse<String>> getValidateCode() {
        // 1. 获取验证码信息
        VerifyCode verifyCode = loginService.getVerifyCode();

        // 2. 创建响应体（包含Base64图片）
        GlobalResponse<String> responseBody = GlobalResponse.success(verifyCode.getImage());

        // 3. 构建完整响应实体（包含头部和响应体）
        return ResponseEntity.ok().header("CAPTCHA", verifyCode.getKey()).body(responseBody);
    }

    @PostMapping("/register")
    public GlobalResponse register(RegisterUserDTO registerUserDTO) {
        userService.register(registerUserDTO);
        return GlobalResponse.success();
    }
}
