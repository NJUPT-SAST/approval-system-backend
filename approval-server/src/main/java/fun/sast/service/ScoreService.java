package fun.sast.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fun.sast.entity.Score;

public interface ScoreService extends IService<Score> {
    public Boolean scorePoint();
    public Integer scoreTatal(String code,Integer comId);
}
