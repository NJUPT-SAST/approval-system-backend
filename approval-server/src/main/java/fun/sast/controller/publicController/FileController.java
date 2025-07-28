package fun.sast.controller.publicController;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class FileController {

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
