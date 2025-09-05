package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fun.sast.entity.Notice;
import fun.sast.entity.User;
import fun.sast.enums.UserRoleEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.mapper.NoticeMapper;
import fun.sast.service.NoticeService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
            // 未登录：获取学生公告
            if (user == null) {
                if (notice.getRole().equals(UserRoleEnum.STUDENT.getRole())
                        && notice.getTime().isBefore(LocalDateTime.now())) {
                    results.add(notice);
                }
                continue;
            }

            // 管理员
            if (user.getRole().equals(UserRoleEnum.ADMIN.getRole())) {
                results.add(notice);
            }

            // 自己角色
            else if (user.getRole().equals(notice.getRole())
                    && notice.getTime().isBefore(LocalDateTime.now())) {
                results.add(notice);
            }
        }
        return results;
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean releaseNotice(Notice notice) {
        // 如果时间为空，设置为当前时间
        if (notice.getTime() == null) {
            notice.setTime(LocalDateTime.parse(LocalDateTime.now().format(formatter)));
        }
        return noticeMapper.insert(notice) > 0;
    }

    @Override
    public boolean editNotice(Notice notice) {
        // 如果时间为空，保持原有时间不变
        if (notice.getTime() != null) {
            notice.setTime(LocalDateTime.parse(LocalDateTime.now().format(formatter)));
        }
        return noticeMapper.update(notice) > 0;
    }

    @Override
    public boolean deleteNotice(Integer id) {
        return noticeMapper.delete(id) > 0;
    }
}
