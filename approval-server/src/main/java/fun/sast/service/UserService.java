package fun.sast.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import fun.sast.controller.publicController.UserResponse;
import fun.sast.dto.UserLoginDTO;
import fun.sast.entity.User;
import fun.sast.vo.UserLoginVO;
import java.util.Map;

public interface UserService {
    /**
     * 验证用户
     *
     * @param code 学号
     * @param password 密码
     */
    User authenticate(String code, String password);

    Map<String, Object> getAllComList(Integer cur, Integer limit);

    Map<String, Object> getSignedComList(User user, Integer cur, Integer limit);

    Map<String, Object> getComInfo(Long comId);

    Map<String, Object> getComSignUpInfo(Long comId);

    Map<String, Object> searchComName(String key, Integer cur, Integer limit);

    Map<String, Object> getTeamInfo(User user, Long comId);

    Map<String, String> getUploadCertificate(User user, Long comId, String input, String filename);

    UserResponse getUserProfile(User user);

    JSONObject getComSchemaTemplate(Long comId);

    JSONArray getComSchema(User user, Long comId);

    void uploadComSchema(User user, Long comId, String jsonData);

    void signUpCom(User user, String jsonData);

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录信息
     * @param captcha 验证码ID
     */
    UserLoginVO login(UserLoginDTO userLoginDTO, String captcha);
}
