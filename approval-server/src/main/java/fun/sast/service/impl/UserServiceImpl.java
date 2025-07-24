package fun.sast.service.impl;

import com.alibaba.fastjson2.JSONObject;
import fun.sast.Exception.BaseException;
import fun.sast.entity.Competition;
import fun.sast.entity.Department;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.mapper.DepartmentMapper;
import fun.sast.mapper.UserMapper;
import fun.sast.service.UserService;
import fun.sast.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired private UserMapper userMapper;

    @Autowired private DepartmentMapper departmentMapper;

    @Autowired private CompetitionMapper competitionMapper;

    /**
     * 验证用户信息
     *
     * @param code 学号
     * @param password 密码
     * @return 用户信息
     */
    @Override
    public User authenticate(String code, String password) {
        //        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //        queryWrapper.eq("code", code).eq("password", password);
        //        return userMapper.selectOne(queryWrapper);
        return new User();
    }

    /**
     * 获取用户信息
     *
     * @param user 用户
     * @return 用户信息
     */
    @Override
    public UserProfileVO getUserProfile(User user) {
        UserProfileVO userProfileVO =
                UserProfileVO.builder()
                        .name(user.getName())
                        .major(user.getMajor())
                        .contact(user.getContact())
                        .code(user.getCode())
                        .build();
        Department department = departmentMapper.selectById(user.getDepId());
        if (department != null) {
            userProfileVO.setDepartmentName(department.getName());
        }
        return userProfileVO;
    }

    /**
     * 获取需要提交的资料表单
     *
     * @param comId 比赛id
     * @return 资料表单
     */
    @Override
    public JSONObject getComSchemaTemplate(String comId) {
        Competition competition = competitionMapper.selectById(comId);
        if (competition == null) {
            throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
        }
        JSONObject table = competition.getTable();
        if (table == null) {
            throw new BaseException(ErrorEnum.SCHEMA_ERROR);
        }
        return table;
    }
}
