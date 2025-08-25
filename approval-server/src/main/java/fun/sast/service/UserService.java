package fun.sast.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import fun.sast.dto.UserLoginDTO;
import fun.sast.entity.User;
import fun.sast.vo.UserLoginVO;
import java.util.Map;

public interface UserService {

    Map<String, Object> getAllComList(Integer cur, Integer limit);

    Map<String, Object> getSignedComList(User user, Integer cur, Integer limit);

    Map<String, Object> getComInfo(Long comId);

    Map<String, Object> getComSignUpInfo(Long comId);

    Map<String, Object> searchComName(String key, Integer cur, Integer limit);

    Map<String, Object> getTeamInfo(User user, Long comId);

    JSONObject getComSchemaTemplate(Long comId);

    JSONArray getComSchema(User user, Long comId);

    void uploadComSchema(User user, Long comId, String jsonData);

    void signUpCom(User user, String jsonData);

    User authenticate(String code, String password);

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录信息
     * @param captcha 验证码ID
     */
    UserLoginVO login(UserLoginDTO userLoginDTO, String captcha);

    /**
     * 验证用户信息
     *
     * @param code 学号
     * @param password 密码
     * @return 用户信息
     */
}
