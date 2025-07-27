package fun.sast.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import fun.sast.entity.User;
import fun.sast.vo.UserProfileVO;

public interface UserService {
    /**
     * 验证用户
     *
     * @param code 学号
     * @param password 密码
     */
    User authenticate(String code, String password);

    /**
     * 获取用户信息
     *
     * @param user 用户
     */
    UserProfileVO getUserProfile(User user);

    /**
     * 获取需要提交的比赛表单
     *
     * @param comId 比赛id
     */
    JSONObject getComSchemaTemplate(Long comId);

    /**
     * 获取已提交的比赛表单
     *
     * @param comId 比赛id
     */
    JSONArray getSubmittedComSchemaTemplate(User user, Long comId);
}
