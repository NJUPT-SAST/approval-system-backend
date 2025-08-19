package fun.sast.controller.userController;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import fun.sast.Exception.BaseException;
import fun.sast.annotation.ResponseResult;
import fun.sast.dto.WorkSchemaDTO;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.service.UserService;
import fun.sast.vo.UserProfileVO;
import java.util.LinkedList;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户信息
     *
     * @return 用户信息
     * @throws BaseException 用户不存在
     */
    @GetMapping("/profile")
    @ResponseResult
    public UserProfileVO getUserProfile() {
        User user = UserInterceptor.userHolder.get();
        if (user == null) {
            throw new BaseException(ErrorEnum.USER_NOT_EXIST);
        }
        return userService.getUserProfile(user);
    }

    /**
     * 获取需要提交的比赛表单
     *
     * @param comId 比赛id
     * @return 比赛表单
     */
    @GetMapping("/com/schema/{comId}")
    public JSONObject getComSchemaTemplate(@PathVariable Long comId) {
        return userService.getComSchemaTemplate(comId);
    }

    /**
     * 获取已提交的比赛表单
     *
     * @param comId 比赛id
     * @return 提交表单
     */
    @GetMapping("/com/getSchema/{comId}")
    public JSONArray getSubmittedComSchemaTemplate(@PathVariable Long comId) {
        User user = UserInterceptor.userHolder.get();
        return userService.getSubmittedComSchemaTemplate(user, comId);
    }

    /**
     * 提交作品资料表单
     *
     * @param comId 比赛id
     * @param workSchemaDTOLinkedList 作品表单
     */
    @PostMapping("/com/uploadSchema/{comId}")
    public void uploadComSchema(
            @PathVariable Long comId,
            @RequestBody LinkedList<WorkSchemaDTO> workSchemaDTOLinkedList) {
        User user = UserInterceptor.userHolder.get();
        userService.uploadComSchema(user, comId, workSchemaDTOLinkedList);
    }

    /**
     * 获取上传作品凭证
     *
     * @param comId 比赛id
     * @param input 输入框名
     * @param filename 文件名
     * @return 上传作品凭证
     */
    @GetMapping("/com/uploadCertificate")
    public Map<String, String> getUploadCertificate(
            @RequestParam Long comId, @RequestParam String input, @RequestParam String filename) {
        User user = UserInterceptor.userHolder.get();
        return userService.getUploadCertificate(user, comId, input, filename);
    }
}
