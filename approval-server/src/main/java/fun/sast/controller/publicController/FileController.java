package fun.sast.controller.publicController;

import fun.sast.entity.User;
import fun.sast.response.GlobalResponse;
import fun.sast.service.FileService;
import fun.sast.service.UserService;
import fun.sast.utils.OSSUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class FileController {

    @Autowired private OSSUtil ossUtil;
    @Autowired private UserService userService;
    @Autowired private FileService fileService;

    @PostMapping("/file/delete")
    public String delete(@RequestParam String url) {
        log.info("收到删除请求，文件URL: {}", url);
        String objectName = fun.sast.utils.FileUtil.getObjectNameOSS(url);
        log.info("对象路径: {}", objectName);
        ossUtil.deleteFileOSS(url);
        return "删除请求已发送，文件路径: " + objectName;
    }

    /**
     * 获取下载凭证
     *
     * @param url URL编码后的文件地址
     */
    @GetMapping("/com/file/downloadCertificate")
    public GlobalResponse downloadCertificate(@RequestParam String url) {
        // User user = UserInterceptor.userHolder.get();
        // return new HashMap<>(){{
        // put("url",fileService.getDownloadCertificate(user, url));}};\

        // 根据url拼接，传入user鉴权
        User user = new User();
        String certificateUrl = fileService.getDownloadCertificate(user, url);

        return GlobalResponse.success(certificateUrl);
    }

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
