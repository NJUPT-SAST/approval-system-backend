package fun.sast.controller.publicController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/com")
public class CommonController {

    /**
     * 获取比赛公告
     *
     * @param id 比赛id
     * @return 比赛通知
     */
    @GetMapping("/notice/list")
    public List<Map<String, Object>> getComNotice(@RequestParam Long id) {
        return new ArrayList<>();
    }

    /**
     * 获取下载凭证
     *
     * @param url 证书url
     */
    @GetMapping("/file/downloadCertificate")
    public Map<String, String> downloadCertificate(@RequestParam String url) {
        return new HashMap<>();
    }
}
