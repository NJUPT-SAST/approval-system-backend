package fun.sast.controller.publicController;

import fun.sast.annotation.ResponseResult;
import fun.sast.dto.UserLoginDTO;
import fun.sast.entity.VerifyCode;
import fun.sast.response.GlobalResponse;
import fun.sast.service.LoginService;
import fun.sast.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    //    @Autowired
    // private UserService userService;
    @Autowired private LoginService loginService;

    /**
     * 登录
     *
     * @param userLoginDTO
     * @return UserLoginVO
     */
    @PostMapping("/login")
    @ResponseResult
    public UserLoginVO login(
            @RequestBody UserLoginDTO userLoginDTO, @RequestHeader String captcha) {

        //        User user = userService.authenticate(userLoginDTO.getCode(),
        // userLoginDTO.getPassword());
        //        if(user == null){
        //            throw new BaseException(ErrorEnum.Login_ERROR);
        //        }
        //        String token = JwtUtil.generateToken(user.getCode());
        //
        //        return UserLoginVO.builder()
        //                .name(user.getName())
        //                .depId(user.getDepId())
        //                .role(user.getRole())
        //                .token(token)
        //                .build();
        return new UserLoginVO();
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
}
