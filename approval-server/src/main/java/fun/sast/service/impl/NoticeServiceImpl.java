package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.Competition;
import fun.sast.entity.Notice;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.enums.UserRoleEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.mapper.NoticeMapper;
import fun.sast.service.NoticeService;
import fun.sast.vo.NoticeOperateVO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private final NoticeMapper noticeMapper;
    private final CompetitionMapper competitionMapper;

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

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void releaseNotice(NoticeOperateVO operateVO, User currentUser) {
        Competition competition = competitionMapper.selectById(operateVO.getComId());
        if (competition == null) {
            throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
        }
        LocalDateTime noticeTime = operateVO.getTime();
        String noticeTimeStr;
        // 前端未传值，设置为当前时间
        if (noticeTime == null) {
            noticeTimeStr = LocalDateTime.now().format(formatter);
        } else {
            noticeTimeStr = noticeTime.toString();
        }

        // 转换为Notice实体，并填充其他字段信息
        Notice notice =
                Notice.builder()
                        .comId(operateVO.getComId())
                        .content(operateVO.getContent())
                        .role(operateVO.getRole())
                        .title(operateVO.getTitle())
                        .time(noticeTime)
                        .createTime(LocalDateTime.now())
                        .updateTime(null)
                        .createUser(currentUser.getCreateUser())
                        .updateUser(null)
                        .build();

        int result = noticeMapper.insert(notice);
        if (result <= 0) {
            throw new BaseException(ErrorEnum.NOTICE_ERROR);
        }
    }

    @Override
    public void updateNotice(NoticeOperateVO operateVO, User currentUser) {
        Notice existingNotice = noticeMapper.selectById(operateVO.getId());
        if (existingNotice == null) {
            throw new BaseException(ErrorEnum.NOTICE_NOT_EXIST);
        }

        LocalDateTime noticeTime = operateVO.getTime();
        String noticeTimeStr;
        // 前端未传值，设置为当前时间
        if (noticeTime == null) {
            noticeTimeStr = LocalDateTime.now().format(formatter);
        } else {
            noticeTimeStr = noticeTime.toString();
        }

        Notice updatedNotice =
                Notice.builder()
                        .id(operateVO.getId())
                        .comId(operateVO.getComId())
                        .content(operateVO.getContent())
                        .role(operateVO.getRole())
                        .title(operateVO.getTitle())
                        .time(LocalDateTime.parse(noticeTimeStr, formatter))
                        .createTime(existingNotice.getCreateTime())
                        .updateTime(LocalDateTime.parse(LocalDateTime.now().format(formatter)))
                        .createUser(existingNotice.getCreateUser())
                        .updateUser(currentUser.getCreateUser())
                        .build();

        int result = noticeMapper.updateById(updatedNotice);
        if (result <= 0) {
            throw new BaseException(ErrorEnum.NOTICE_ERROR);
        }
    }

    @Override
    public void deleteNotice(Integer noticeId, User currentUser) {
        Notice existingNotice = noticeMapper.selectById(noticeId);
        if (existingNotice == null) {
            throw new BaseException(ErrorEnum.NOTICE_NOT_EXIST);
        }

        int result = noticeMapper.deleteById(noticeId);
        if(result <= 0) {
            throw new BaseException(ErrorEnum.NOTICE_ERROR);
        }
    }
}
