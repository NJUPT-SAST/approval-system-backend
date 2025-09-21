package fun.sast.controller.adminController;

import fun.sast.annotation.ResponseResult;
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
        scoreService.exportScore(comId, response);
    }
}
