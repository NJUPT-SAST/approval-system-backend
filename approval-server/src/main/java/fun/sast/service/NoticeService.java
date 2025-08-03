package fun.sast.service;

import fun.sast.entity.Notice;
import java.util.List;

public interface NoticeService {
    List<Notice> getNotices(String comId);
}
