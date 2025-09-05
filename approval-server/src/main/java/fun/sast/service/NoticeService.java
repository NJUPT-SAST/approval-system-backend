package fun.sast.service;

import fun.sast.entity.Notice;
import fun.sast.entity.User;
import fun.sast.vo.NoticeOperateVO;
import java.util.List;

public interface NoticeService {
    List<Notice> getNotices(String comId);

    /** 发布公告 */
    void releaseNotice(NoticeOperateVO operateVO, User currentUser);

    /** 修改公告 */
    void updateNotice(NoticeOperateVO operateVO, User currentUser);

    /** 删除公告 */
    void deleteNotice(Integer noticeId, User currentUser);
}
