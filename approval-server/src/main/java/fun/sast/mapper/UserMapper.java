package fun.sast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {}
