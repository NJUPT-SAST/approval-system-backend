package fun.sast.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.Competition;
import fun.sast.entity.Score;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreMapper extends BaseMapper<Score> {

}
