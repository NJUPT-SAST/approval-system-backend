package fun.sast.service.impl;

import fun.sast.service.ReviewService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Override
    public void exportReviewResult(Integer comId, HttpServletResponse response) {}
}
