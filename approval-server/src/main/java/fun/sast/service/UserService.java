package fun.sast.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import fun.sast.dto.UserLoginDTO;
import fun.sast.dto.WorkSchemaDTO;
import fun.sast.entity.User;
import fun.sast.vo.UserLoginVO;
import fun.sast.vo.UserProfileVO;
import java.util.LinkedList;
import java.util.Map;

public interface UserService {

    /**
     * 获取用户信息
     *
     * @param user 用户
     * @return 用户信息
     */
    UserProfileVO getUserProfile(User user);

    /**
     * 获取需要提交的比赛表单
     *
     * @param comId 比赛id
     * @return 需要提交的比赛表单
     */
    JSONObject getComSchemaTemplate(Long comId);

    /**
     * 获取已提交的比赛表单
     *
     * @param comId 比赛id
     * @return 已提交表单
     */
    JSONArray getSubmittedComSchemaTemplate(User user, Long comId);

    /**
     * 获取上传作品凭证
     *
     * @param user 用户
     * @param id 比赛id
     * @param input 输入框名
     * @param filename 文件名 return 上传凭证
     */
    Map<String, String> getUploadCertificate(User user, Long id, String input, String filename);

    /**
     * 登录
     *
     * @param userLoginDTO 登录信息
     * @param captcha 验证码
     * @return 登录信息
     */
    UserLoginVO login(UserLoginDTO userLoginDTO, String captcha);

    /**
     * 上传比赛表单
     *
     * @param user 用户
     * @param comId 比赛id
     * @param workSchemaDTOLinkedList 表单
     */
    void uploadComSchema(User user, Long comId, LinkedList<WorkSchemaDTO> workSchemaDTOLinkedList);
}
