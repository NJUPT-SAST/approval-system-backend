package fun.sast.controller.publicController;

import fun.sast.entity.Notice;
import fun.sast.response.GlobalResponse;
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

    /**
     * 获取比赛公告
     *
     * @param id 比赛id
     * @return 比赛通知
     */
    @GetMapping("/notice/list")
    public GlobalResponse<List<Notice>> getComNotice(@RequestParam Long id) {
        System.out.println("Received competition ID: " + id);

        Notice notice = new Notice();
        notice.setId(1);
        notice.setTitle("比赛公告");
        notice.setContent("比赛即将开始");
        notice.setTime("2023-06-20 10:00:00");
        notice.setRole(1);

        // 使用单元素列表
        return GlobalResponse.success(Collections.singletonList(notice));
    }
}
