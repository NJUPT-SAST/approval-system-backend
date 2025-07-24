package fun.sast.controller.publicController;

import fun.sast.annotation.ResponseResult;
import fun.sast.dto.UserLoginDTO;
import fun.sast.entity.VerifyCode;
import fun.sast.response.GlobalResponse;
import fun.sast.service.LoginService;
import fun.sast.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
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
    public GlobalResponse getValidateCode() {
        VerifyCode verifyCode = loginService.getVerifyCode();
        System.out.println(verifyCode);
        return GlobalResponse.success(verifyCode.getImage() + verifyCode.getText());
    }
}
