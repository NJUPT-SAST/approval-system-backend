package fun.sast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.WhiteList;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface WhiteListMapper extends BaseMapper<WhiteList> {
}
