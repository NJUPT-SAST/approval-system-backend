package fun.sast.controller;

import fun.sast.annotation.ResponseResult;
import fun.sast.entity.Notice;
import fun.sast.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping("/release")
    @ResponseResult
    public boolean releaseNotice(@RequestHeader("Token") String token, @RequestBody Notice notice) {
        return noticeService.releaseNotice(notice);
    }

    @PostMapping("/edit")
    @ResponseResult
    public boolean editNotice(@RequestHeader("Token") String token, @RequestBody Notice notice) {
        return noticeService.editNotice(notice);
    }

    @PostMapping("/del")
    @ResponseResult
    public boolean deleteNotice(@RequestHeader("Token") String token, @RequestParam Integer id) {
        return noticeService.deleteNotice(id);
    }
}
