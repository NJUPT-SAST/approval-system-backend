package fun.sast.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.*;
import fun.sast.enums.ErrorEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.mapper.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExcelForJudgeAssignUtil extends AnalysisEventListener<Map<Integer, String>> {

    private final JudgeMapper judgeMapper;
    private final WorkMapper workMapper;
    private final UserInterceptor userInterceptor;
    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;

    /** -- GETTER -- 获取解析结果 Map key = 作品ID value = 对应评委ID列表 */
    // 用于返回结果
    @Getter private final Map<Long, List<String>> workJudgeMap = new HashMap<>();

    private final CompetitionMapper competitionMapper;

    @Override
    public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
        // 第 1 列：作品 id
        Long workId = Long.valueOf(rowData.get(0));

        // 查作品表，获取 user_code（队长学号）
        Work work = workMapper.selectById(workId);
        if (work == null) {
            log.error("作品ID {} 不存在", workId);
            throw new BaseException(ErrorEnum.WORK_NOT_EXIST);
        }
        String captainCode = work.getUserCode();

        // 查用户表，把学号转成 userId
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.eq("code", captainCode);
        User captain = userMapper.selectOne(userQuery);
        if (captain == null) throw new BaseException(ErrorEnum.USER_NOT_EXIST);
        Integer captainId = captain.getId();

        // 活动id
        Long comId = work.getComId();
        QueryWrapper<Review> reviewQueryWrapper = new QueryWrapper<>();
        reviewQueryWrapper.eq("com_id", comId).eq("user_id", captainId);
        Review review = reviewMapper.selectOne(reviewQueryWrapper);
        QueryWrapper<Competition> competitionQueryWrapper = new QueryWrapper<>();
        competitionQueryWrapper.eq("id", comId).select("is_review");
        Competition competition = competitionMapper.selectOne(competitionQueryWrapper);

        System.out.println(review);
        System.out.println(competition);

        // 第 3 列及以后：评委学号
        List<String> newJudgeCodes = new ArrayList<>();
        for (int i = 2; i < rowData.size(); i++) {
            String judgeCode = rowData.get(i);
            if (judgeCode != null && !judgeCode.isBlank()) {
                newJudgeCodes.add(judgeCode.trim());
            }
        }

        // 查询数据库中已有的评委
        List<Judge> oldJudges =
                judgeMapper.selectList(
                        new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Judge>()
                                .eq("com_id", work.getComId())
                                .eq("captain_code", captainCode));
        Set<String> oldJudgeSet =
                oldJudges.stream().map(Judge::getJudgeCode).collect(Collectors.toSet());
        Set<String> newJudgeSet = new HashSet<>(newJudgeCodes);

        // 需要删除的评委
        Set<String> toDelete = new HashSet<>(oldJudgeSet);
        toDelete.removeAll(newJudgeSet);

        // 需要新增的评委
        Set<String> toInsert = new HashSet<>(newJudgeSet);
        toInsert.removeAll(oldJudgeSet);

        // 执行删除
        for (String judgeCode : toDelete) {
            judgeMapper.delete(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Judge>()
                            .eq("com_id", work.getComId())
                            .eq("captain_code", captainCode)
                            .eq("judge_code", judgeCode));
            log.info("删除已移除评委: {} 对作品 {}", judgeCode, workId);
        }

        // 执行新增
        for (String judgeCode : toInsert) {
            Judge judge = new Judge();
            judge.setComId(work.getComId());
            judge.setCaptainCode(captainCode);
            judge.setJudgeCode(judgeCode);
            judge.setCreateTime(LocalDateTime.now());
            judge.setUpdateTime(LocalDateTime.now());
            judge.setCreateUser(0L);
            judge.setUpdateUser(0L);
            judgeMapper.insert(judge);
            log.info("新增评委: {} 对作品 {}", judgeCode, workId);
        }

        // 更新 Map
        workJudgeMap.put(workId, newJudgeCodes);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel 数据全部解析完成！");
    }
}
