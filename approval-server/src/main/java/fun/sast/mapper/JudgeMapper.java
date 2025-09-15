package fun.sast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.Judge;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JudgeMapper extends BaseMapper<Judge> {
    @Delete("DELETE FROM judge WHERE captain_code = #{captainCode} AND com_id = #{comId}")
    void deleteByWorkId(@Param("workId") Long workId);
}
