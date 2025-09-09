package fun.sast.controller;

import fun.sast.annotation.ResponseResult;
import fun.sast.entity.Notice;
import fun.sast.service.NoticeService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
