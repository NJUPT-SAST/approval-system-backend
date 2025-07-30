package fun.sast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.File;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMapper extends BaseMapper<File> {}
