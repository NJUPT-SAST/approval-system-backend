package fun.sast.controller.adminController;

import fun.sast.Exception.BaseException;
import fun.sast.annotation.ResponseResult;
import fun.sast.dto.FileResponseDTO;
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
    @ResponseResult
    public FileResponseDTO exportComInfo(
            HttpServletResponse response, @RequestParam Integer comId) {
        User currentUser = UserInterceptor.userHolder.get();

        if (currentUser.getRole() != 3) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }
        try {
            return fileService.exportComInfo(comId);
        } catch (Exception e) {

            throw new BaseException(ErrorEnum.EXPORT_COMINFO_ERROR);
        }
    }

    @ResponseResult
    @GetMapping("/exportWork")
    public void exportWork(
            HttpServletResponse response,
            @RequestParam Integer comId,
            @RequestParam String userCode) {
        User currentUser = UserInterceptor.userHolder.get();
        if (currentUser.getRole() != 3) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }
        fileService.exportWork(response, comId, userCode);
    }
}
