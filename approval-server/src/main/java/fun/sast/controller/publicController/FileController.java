package fun.sast.controller.publicController;

import fun.sast.annotation.ResponseResult;
import fun.sast.entity.User;
import fun.sast.response.GlobalResponse;
import fun.sast.service.FileService;
import fun.sast.service.UserService;
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
    private final UserService userService;
    private final FileService fileService;

    /**
     * 获取下载凭证
     *
     * @param url 文件地址例如https://baiyaoshi.oss-cn-hangzhou.aliyuncs.com/list/list2/text2.txt
     */
    @ResponseResult
    @GetMapping("/com/file/downloadCertificate")
    public GlobalResponse downloadCertificate(@RequestParam String url) {
        User user = new User();
        String certificateUrl = fileService.getDownloadCertificate(user, url);

        return GlobalResponse.success(certificateUrl);
    }

    /**
     * @param url 原始url
     * @param response 重定向至下载
     * @throws IOException
     */
    @GetMapping("/com/file/download")
    public void download(@RequestParam String url, HttpServletResponse response)
            throws IOException {
        // 判断是否是 OSS 文件地址
        if (!ossUtil.isOSSBucketURL(url)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid OSS URL.");
            return;
        }
        // 获取带签名的下载链接
        String signedUrl = ossUtil.getDownloadCertificate(url);
        System.out.println(signedUrl);
        // 重定向到签名地址进行下载
        response.sendRedirect(signedUrl);
    }
}
