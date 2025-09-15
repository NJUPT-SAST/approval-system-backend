package fun.sast.controller;

import fun.sast.annotation.RateLimited;
import fun.sast.annotation.ResponseResult;
import fun.sast.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/com/file")
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 获取下载凭证
     *
     * @param url 文件地址例如https://mock-bucket.cos.ap-nanjing.myqcloud.com/list/list2/text2.txt
     */
    @ResponseResult
    @GetMapping("/downloadCertificate")
    public String downloadCertificate(@RequestParam String url) {

        return fileService.getDownloadCertificate(url);
    }

    /**
     * @param url 原始url
     * @param response 重定向至下载
     * @throws IOException IO异常
     */
    @GetMapping("/download")
    @RateLimited
    public void download(@RequestParam String url, HttpServletResponse response)
            throws IOException {
        // 获取带签名的下载链接
        String signedUrl = fileService.getDownloadCertificate(url);
        // 重定向到签名地址进行下载
        response.sendRedirect(signedUrl);
    }
}
