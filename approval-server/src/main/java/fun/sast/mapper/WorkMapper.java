package fun.sast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.Work;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkMapper extends BaseMapper<Work> {
    List<Work> getWorks(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize, @Param("comId") Long comId);
}
