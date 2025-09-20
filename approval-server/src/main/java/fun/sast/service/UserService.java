package fun.sast.service;

import fun.sast.dto.UserLoginDTO;
import fun.sast.entity.User;
import fun.sast.vo.UserLoginVO;
import java.util.Map;

public interface UserService {

    Map<String, Object> getAllComList(Integer cur, Integer limit);

    Map<String, Object> getSignedComList(User user, Integer cur, Integer limit);

    Map<String, Object> getComInfo(Long comId);

    /**
     * 获取用户在指定比赛中的报名信息
     *
     * @param user 当前登录用户
     * @param comId 比赛ID
     * @return 报名信息
     */
    Map<String, Object> getComSignUpInfo(User user, Long comId);

    Map<String, Object> searchComName(String key, Integer cur, Integer limit);

    Map<String, Object> getTeamInfo(User user, Long comId);

    void signUpCom(User user, String jsonData);

    /**
     * 修改比赛报名信息
     *
     * @param user 当前登录用户
     * @param jsonData 修改的报名信息
     */
    void updateComSignUpInfo(User user, String jsonData);

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录信息
     * @param captcha 验证码ID
     */
    UserLoginVO login(UserLoginDTO userLoginDTO, String captcha);
}
