package fun.sast.controller.adminController;

import fun.sast.Exception.BaseException;
import fun.sast.annotation.ResponseResult;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.service.NoticeService;
import fun.sast.vo.NoticeOperateVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 发布公告
     *
     * @param vo
     * @param request
     * @return
     */
    @PostMapping("/release")
    @ResponseResult
    public void releaseNotice(@RequestBody NoticeOperateVO vo, HttpServletRequest request) {
        User currentUser = UserInterceptor.userHolder.get();
        if (currentUser.getRole() != 3) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }
        try {
            noticeService.releaseNotice(vo, currentUser);
        } catch (Exception e) {
            throw new BaseException(ErrorEnum.NOTICE_ERROR);
        }
    }

    /**
     * 修改公告
     *
     * @param vo
     * @return
     */
    @PostMapping("/edit")
    @ResponseResult
    public void updateNotice(@RequestBody NoticeOperateVO vo) {
        User currentUser = UserInterceptor.userHolder.get();
        if (currentUser.getRole() != 3) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }
        try {
            noticeService.updateNotice(vo, currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ErrorEnum.NOTICE_ERROR);
        }
    }

    /**
     * 删除公告
     *
     * @param id 公告id
     * @return
     */
    @PostMapping("/del")
    @ResponseResult
    public void deleteNotice(@RequestParam Integer id) {
        User currentUser = UserInterceptor.userHolder.get();
        if (currentUser.getRole() != 3) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }
        try {
            noticeService.deleteNotice(id, currentUser);
        } catch (Exception e) {
            throw new BaseException(ErrorEnum.NOTICE_ERROR);
        }
    }
}
