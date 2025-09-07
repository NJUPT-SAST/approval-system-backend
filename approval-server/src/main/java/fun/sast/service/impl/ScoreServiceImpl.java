package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.sast.entity.Competition;
import fun.sast.entity.Review;
import fun.sast.entity.Score;
import fun.sast.mapper.ScoreMapper;
import fun.sast.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

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

    @Override
    public Integer scoreTatal(String code,Integer comId) {
        QueryWrapper<Score> wrapper = new QueryWrapper<Score>()
                .eq("judge_id",code)
                .eq("com_id",comId);
        Integer count = Math.toIntExact(scoreMapper.selectCount(wrapper));
        return count;
    }

    @Override
    public boolean scoreUpload(Integer id, Integer score, String opinion) {
        QueryWrapper<Score> wrapper=new QueryWrapper<Score>()
                .select("com_id","user_id")
                .eq("id",id);

        Score score1=scoreMapper.selectById(wrapper);
        String comId = score1.getComId();
        String userId = score1.getUserId();

        Score score2 = new Score();
        score2.setScore(score);
        score2.setOption(opinion);

        if (userId != null && comId != null) {
           scoreMapper.update(score2,wrapper);
           return true;
        }
        return false;

    }
}
