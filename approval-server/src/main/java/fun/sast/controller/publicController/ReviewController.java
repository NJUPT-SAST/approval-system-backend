package fun.sast.controller.publicController;

import fun.sast.entity.User;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.response.GlobalResponse;
import fun.sast.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private  final ReviewService reviewService;


    //待审核总数
    @GetMapping("/review/total")
    public Object reviewTotal(Integer comId){
        User user = UserInterceptor.userHolder.get();
        return GlobalResponse.success(reviewService.reviewTotal(user.getCode(), comId));
    }



}
