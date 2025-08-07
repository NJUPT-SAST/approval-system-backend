package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fun.sast.entity.Notice;
import fun.sast.entity.User;
import fun.sast.enums.UserRoleEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.mapper.NoticeMapper;
import fun.sast.service.NoticeService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private final NoticeMapper noticeMapper;

    // 已经登陆显示相应角色的公告
    // 未登录获取普通角色的公告
    // 管理员显示所有公告（未推送）
    // 非管理员显示已经推送的

    /**
     * @param comId 比赛id
     * @return 根据身份返回公告
     */
    @Override
    public List<Notice> getNotices(String comId) {
        // 鉴权
        User user = UserInterceptor.userHolder.get();

        List<Notice> notices =
                noticeMapper.selectList(
                        new LambdaQueryWrapper<Notice>().eq(Notice::getComId, comId));
        List<Notice> results = new ArrayList<>();

        for (Notice notice : notices) {
            // 未登录：只能看游客权限的，已推送的公告
            if (user == null) {
                if (notice.getRole().equals(UserRoleEnum.TOURIST.getRole())
                        && notice.getTime().isBefore(LocalDateTime.now())) {
                    results.add(notice);
                }
                continue;
            }

            // 管理员
            if (user.getRole().equals(UserRoleEnum.ADMIN.getRole())) {
                results.add(notice);
            }
            // 游客
            else if (user.getRole().equals(UserRoleEnum.TOURIST.getRole())) {
                if (notice.getRole().equals(UserRoleEnum.TOURIST.getRole())
                        && notice.getTime().isBefore(LocalDateTime.now())) {
                    results.add(notice);
                }
            }
            // 自己角色
            else if (user.getRole().equals(notice.getRole())
                    && notice.getTime().isBefore(LocalDateTime.now())) {
                results.add(notice);
            }
        }
        return results;
    }
}
