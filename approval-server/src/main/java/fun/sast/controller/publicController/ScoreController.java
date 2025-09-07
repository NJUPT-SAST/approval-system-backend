package fun.sast.controller.publicController;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.response.GlobalResponse;
import fun.sast.service.ScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ScoreController {
    public final ScoreService scoreService;

    //评分红点
    @GetMapping("/score/red-point")
    public Object scorePoint(){
        return GlobalResponse.success(scoreService.scorePoint());
    }

    @GetMapping("/score/total")
    public Object scoreTotal(Integer comId){
        User user = UserInterceptor.userHolder.get();
        return GlobalResponse.success(scoreService.scoreTatal(user.getCode(),comId));
    }

    @PostMapping("/score/upload")
    public Object scoreUpload(Integer id, Integer score, @RequestParam(value = "opinion",required = false)String opinion){
        if (scoreService.scoreUpload(id, score, opinion)) {
            return GlobalResponse.success();
        }
        return GlobalResponse.failure(ErrorEnum.COMMON_ERROR);
    }
}
