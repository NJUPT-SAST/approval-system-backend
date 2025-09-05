package fun.sast.controller.userController;

import fun.sast.Exception.BaseException;
import fun.sast.annotation.ResponseResult;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.service.NoticeService;
import fun.sast.vo.NoticeOperateVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notice")
public class NoticeController {

    @Autowired private NoticeService noticeService;

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
    @PutMapping("/update")
    @ResponseResult
    public void updateNotice(@RequestBody NoticeOperateVO vo) {
        User currentUser = UserInterceptor.userHolder.get();
        try {
            noticeService.updateNotice(vo, currentUser);
        } catch (Exception e) {
            throw new BaseException(ErrorEnum.NOTICE_ERROR);
        }
    }

    /**
     * 删除公告
     *
     * @param id 公告id
     * @return
     */
    @DeleteMapping("/del")
    @ResponseResult
    public void deleteNotice(@PathVariable Integer id) {
        User currentUser = UserInterceptor.userHolder.get();
        try {
            noticeService.deleteNotice(id, currentUser);
        } catch (Exception e) {
            throw new BaseException(ErrorEnum.NOTICE_ERROR);
        }
    }
}
