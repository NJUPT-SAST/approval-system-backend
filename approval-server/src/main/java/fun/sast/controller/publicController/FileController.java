package fun.sast.controller.publicController;

import fun.sast.annotation.ResponseResult;
import fun.sast.service.FileService;
import fun.sast.utils.FileUtil;
import fun.sast.utils.OSSUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final OSSUtil ossUtil;
    private final FileService fileService;
    private final FileUtil fileUtil;

    /**
     * 获取下载凭证
     *
     * @param url 文件地址例如https://baiyaoshi.oss-cn-hangzhou.aliyuncs.com/list/list2/text2.txt
     */
    @ResponseResult
    @GetMapping("/com/file/downloadCertificate")
    public String downloadCertificate(@RequestParam String url) {
        String certificateUrl = fileService.getDownloadCertificate(url);
        return certificateUrl;
    }

    /**
     * @param url 原始url
     * @param response 重定向至下载
     * @throws IOException
     */
    @GetMapping("/com/file/download")
    public void download(@RequestParam String url, HttpServletResponse response)
            throws IOException {
        // 获取带签名的下载链接
        String signedUrl = fileService.getDownloadCertificate(url);
        // 重定向到签名地址进行下载
        response.sendRedirect(signedUrl);
    }
}
