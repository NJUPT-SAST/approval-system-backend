package fun.sast.controller.adminController;

import fun.sast.annotation.ResponseResult;
import fun.sast.service.ReviewService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @ResponseResult
    @GetMapping("/data/result")
    public void exportReviewResult(@RequestParam Integer comId, HttpServletResponse response) {
        reviewService.exportReviewResult(comId, response);
    }
}
