package com.sast.approval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sast.approval.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}
