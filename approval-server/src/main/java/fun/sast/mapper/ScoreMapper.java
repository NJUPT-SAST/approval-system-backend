package fun.sast.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.Competition;
import fun.sast.entity.Score;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreMapper extends BaseMapper<Score> {


    Integer getJCount(@Param("code") String code, @Param("id") Integer id);
    Integer getComIdByProId(@Param("proId") Integer proId);
    String getUserCode(Integer proId);

    Integer getScoreInfo(@Param("comId") Integer comId, @Param("user") String userCode, @Param("judge") String judgeCode);

    String getOpinionInfo(@Param("comId") Integer comId, @Param("user") String userCode, @Param("judge") String judgeCode);
}
