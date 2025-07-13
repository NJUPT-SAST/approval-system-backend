package fun.sast.controller.publicController;


import fun.sast.annotation.ResponseResult;

import fun.sast.dto.UserLoginDTO;
import fun.sast.vo.UserLoginVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

//    @Autowired
//    private UserService userService;

    /**
     * 登录
     * @param userLoginDTO
     * @return UserLoginVO
     */

    @PostMapping("/login")
    @ResponseResult
    public UserLoginVO login(@RequestBody UserLoginDTO userLoginDTO, @RequestHeader String captcha) {
//        User user = userService.authenticate(userLoginDTO.getCode(), userLoginDTO.getPassword());
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
        return  new UserLoginVO();

    }
}
