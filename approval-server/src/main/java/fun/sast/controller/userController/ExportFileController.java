package fun.sast.controller.userController;

import fun.sast.Exception.BaseException;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/data")
@RequiredArgsConstructor
public class ExportFileController {
    private final FileService fileService;

    @GetMapping("/exportComInfo")
    public void exportComInfo(HttpServletResponse response, @RequestParam Long comId) {
        User currentUser = UserInterceptor.userHolder.get();
        if (currentUser.getRole() != 3) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }
        fileService.exportComInfo(response, comId);
    }

    @GetMapping("/exportWork")
    public void exportWork(HttpServletResponse response, @RequestParam Long comId,@RequestParam String userCode) {
        User currentUser = UserInterceptor.userHolder.get();
        if (currentUser.getRole() != 3) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }
        fileService.exportWork(response, comId, userCode);
    }
}
