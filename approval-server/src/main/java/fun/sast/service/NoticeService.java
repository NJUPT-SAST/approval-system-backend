package fun.sast.service;

import fun.sast.entity.Notice;

import java.util.List;

public interface NoticeService {
    List<Notice> getNotices(String comId);

    /**
     * 发布公告
     */
    boolean releaseNotice(Notice notice);

    /**
     * 修改公告
     */
    boolean editNotice(Notice notice);

    /**
     * 删除公告
     */
    boolean deleteNotice(Integer id);
}
