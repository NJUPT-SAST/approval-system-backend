package fun.sast.controller.userController;

import fun.sast.Exception.BaseException;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.service.ComInfoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ComInfoController {
    private final ComInfoService comInfoService;

    @GetMapping("/admin/data/exportComInfo")
    public void exportComInfo(HttpServletResponse response,@RequestParam Long comId)  {
        User currentUser = UserInterceptor.userHolder.get();
        if(currentUser.getRole() != 3) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }
        comInfoService.exportComInfo(response,comId);
    }
}
