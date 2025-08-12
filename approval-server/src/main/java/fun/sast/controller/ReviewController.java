package fun.sast.controller;

import fun.sast.service.ReviewService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class ReviewController {

    @Autowired private ReviewService reviewService;

    @GetMapping("/data/result")
    public void exportReviewResult(@RequestParam Integer comId, HttpServletResponse response) {
        reviewService.exportReviewResult(comId, response);
    }
}
