package fun.sast.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fun.sast.entity.Score;
import fun.sast.vo.ProgramInfoForScore;
import org.springframework.web.bind.annotation.RequestParam;

public interface ScoreService extends IService<Score> {
    public Boolean scorePoint();
    Boolean confirmPro(String code,Integer id);
    ProgramInfoForScore getProgramInfo(Integer proId, String judgeCode);
    public Integer scoreTatal(String code,Integer comId);
    public boolean scoreUpload(Integer id, Integer score, @RequestParam(value = "opinion",required = false)String opinion);
}
