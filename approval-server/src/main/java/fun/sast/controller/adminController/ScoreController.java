package fun.sast.controller.adminController;

import fun.sast.Exception.BaseException;
import fun.sast.annotation.ResponseResult;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.service.ScoreService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    @ResponseResult
    @GetMapping("/data/result")
    public void exportScore(@RequestParam Integer comId, HttpServletResponse response) {
        User currentUser = UserInterceptor.userHolder.get();
        if (currentUser.getRole() != 3) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }
        scoreService.exportScore(comId, response);
    }
}
