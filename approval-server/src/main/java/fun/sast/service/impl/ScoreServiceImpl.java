package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.sast.entity.Competition;
import fun.sast.entity.Score;
import fun.sast.mapper.ScoreMapper;
import fun.sast.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {

    private final ScoreMapper scoreMapper;
    @Override
    public Boolean scorePoint() {
        QueryWrapper<Score> wrapper = new QueryWrapper<Score>()
                .select("score")
                .eq("score",null);
        Long count = scoreMapper.selectCount(wrapper);
        return count==0 ;
    }
}
