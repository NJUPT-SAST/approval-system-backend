package com.sast.approval.controller.publicController;

import com.sast.approval.Exception.BaseException;
import com.sast.approval.annotation.ResponseResult;
import com.sast.approval.dto.UserLoginDTO;
import com.sast.approval.entity.User;
import com.sast.approval.enums.ErrorEnum;
import com.sast.approval.service.UserService;
import com.sast.approval.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseResult
    public Object login(@RequestBody UserLoginDTO userLoginDTO, @RequestHeader String captcha) {
        User user = userService.authenticate(userLoginDTO.getCode(), userLoginDTO.getPassword());
        if(user == null){
            throw new BaseException(ErrorEnum.Login_ERROR);
        }
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .name(user.getName())
                .depId(user.getDepId())
                .role(user.getRole())
                .token();
        return userLoginVO;
    }
}
