package fun.sast.controller.publicController;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.response.GlobalResponse;
import fun.sast.service.ScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ScoreController {
    public final ScoreService scoreService;

    //评分红点
    @GetMapping("/score/red-point")
    public Object scorepoint(){
        return GlobalResponse.success(scoreService.scorePoint());
    }

}
