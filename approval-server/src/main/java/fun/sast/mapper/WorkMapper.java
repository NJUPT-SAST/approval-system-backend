package fun.sast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.Work;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkMapper extends BaseMapper<Work> {
    List<Work> getWorks(
            @Param("pageNum") Integer pageNum,
            @Param("pageSize") Integer pageSize,
            @Param("comId") Long comId);
}
