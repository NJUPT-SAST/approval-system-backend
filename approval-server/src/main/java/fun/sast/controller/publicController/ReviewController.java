package fun.sast.controller.publicController;

import fun.sast.response.GlobalResponse;
import fun.sast.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ReviewController {

    @Autowired
    private ReviewService reviewService;






}
