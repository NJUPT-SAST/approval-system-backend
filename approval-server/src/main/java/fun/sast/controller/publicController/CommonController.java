package fun.sast.controller.publicController;

import fun.sast.annotation.ResponseResult;
import fun.sast.entity.Notice;
import fun.sast.service.NoticeService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/com")
public class CommonController {
    private final NoticeService noticeService;

    /**
     * 获取比赛公告
     *
     * @param id 比赛id
     * @return 比赛通知
     */
    @ResponseResult
    @GetMapping("/notice/list")
    public List<Notice> noticeList(@RequestParam String id) {
        return noticeService.getNotices(id);
    }
}
