package fun.sast.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.Department;
import fun.sast.entity.Score;
import fun.sast.entity.User;
import fun.sast.entity.Work;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.DepartmentMapper;
import fun.sast.mapper.ScoreMapper;
import fun.sast.mapper.UserMapper;
import fun.sast.mapper.WorkMapper;
import fun.sast.service.ScoreService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreServiceImpl implements ScoreService {

    private final ScoreMapper scoreMapper;
    private final WorkMapper workMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;

    private final ObjectMapper objectMapper; // 用于解析Work的schemaContent字段

    @Override
    public void exportScore(Integer comId, HttpServletResponse response) {
        try {
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName =
                    URLEncoder.encode(
                                    "review-result_" + comId + "_" + System.currentTimeMillis(),
                                    StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20");
            response.setHeader(
                    "Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 获取数据
            Map<String, Object> dataMap = dataList(comId);
            Integer maxJudgeNum = (Integer) dataMap.get("maxJudgeNum");
            List<List<Object>> excelData = (List<List<Object>>) dataMap.get("excelData");

            // 写入Excel
            EasyExcel.write(response.getOutputStream())
                    .head(head(maxJudgeNum)) // 动态表头
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("评审结果")
                    .doWrite(excelData);

            log.info("比赛ID {} 评审结果导出成功", comId);
        } catch (Exception e) {
            log.error("比赛ID {} 评审结果导出失败: {}", comId, e.getMessage());
            throw new BaseException(ErrorEnum.SCORE_NOT_EXIST);
        }
    }

    /**
     * 按最大评委数动态生成表头
     *
     * @param maxJudgeNum 最大评委数
     * @return
     */
    private List<List<String>> head(Integer maxJudgeNum) {
        // 动态生成表头
        List<List<String>> head = new java.util.ArrayList<>();
        // 固定表头
        head.add(List.of("作品ID"));
        head.add(List.of("作品名称"));
        head.add(List.of("队长学号"));
        head.add(List.of("队长姓名"));
        head.add(List.of("队长所在部门"));
        head.add(List.of("项目组别"));
        // 动态表头
        for (int i = 1; i <= maxJudgeNum; i++) {
            head.add(List.of("评委" + i + "工号"));
            head.add(List.of("评委" + i + "评分"));
            head.add(List.of("评委" + i + "意见"));
        }
        return head;
    }

    /**
     * 组装Excel数据
     *
     * @param comId
     * @return Map: {maxJudgeNum, excelData}
     */
    private Map<String, Object> dataList(Integer comId) {
        // 获取该比赛所有作品的评分数据
        QueryWrapper<Score> scoreQueryWrapper = new QueryWrapper<>();
        scoreQueryWrapper
                .eq("com_id", comId)
                .select("id", "com_id", "judge_id", "option", "score", "user_id");
        List<Score> allScores = scoreMapper.selectList(scoreQueryWrapper);
        if (allScores.isEmpty()) {
            throw new BaseException(ErrorEnum.SCORE_NOT_EXIST);
        }
        log.debug("比赛ID {} 共有 {} 条评分记录", comId, allScores.size());

        // 按队长Id分组：key: userId, value: 该队长的所有评审记录List<Score>
        Map<String, List<Score>> scoreGroupByUserId =
                allScores.stream().collect(Collectors.groupingBy(Score::getUserId));

        // 统计最大评委数用于生成表头
        int maxJudgeNum = 0;
        for (List<Score> scores : scoreGroupByUserId.values()) {
            if (scores.size() > maxJudgeNum) {
                maxJudgeNum = scores.size();
            }
        }

        // 组装Excel数据
        List<List<Object>> excelData = new ArrayList<>();
        QueryWrapper<Work> workQueryWrapper = new QueryWrapper<>();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();

        for (Map.Entry<String, List<Score>> entry : scoreGroupByUserId.entrySet()) {
            String userId = entry.getKey();
            List<Score> workScores = entry.getValue();
            List<Object> rowData = ListUtils.newArrayList();

            // 固定列： 作品信息，通过userId查work
            workQueryWrapper
                    .eq("com_id", comId)
                    .eq("user_code", userId)
                    .select("id", "work_name", "schema_content");
            Work work = workMapper.selectOne(workQueryWrapper);
            if (work == null) {
                log.error("未找到队长ID {} 的作品信息", userId);
                throw new BaseException(ErrorEnum.WORK_NOT_EXIST);
            }

            log.debug("队长ID {} 作品ID {} 共有 {} 条评分记录", userId, work.getId(), workScores.size());

            workQueryWrapper.clear();
            rowData.add(work.getId());

            // 固定列：队长信息，通过userId查user+department
            userQueryWrapper.eq("code", userId).select("code", "dep_id");
            User leader = userMapper.selectOne(userQueryWrapper);
            if (leader == null) {
                log.error("未找到队长ID {} 的用户信息", userId);
                throw new BaseException(ErrorEnum.USER_NOT_EXIST);
            }

            log.debug("队长ID {} 姓名 {} 部门ID {}", userId, leader.getName(), leader.getDepId());

            userQueryWrapper.clear();

            String leaderName = null;
            String departmentName = null;
            leaderName = leader.getName() != null ? leader.getName() : "未知姓名";
            // 获取部门名称
            if (leader.getDepId() != null) {
                departmentQueryWrapper.eq("id", leader.getDepId()).select("name");
                Department department = departmentMapper.selectOne(departmentQueryWrapper);
                departmentName = department != null ? department.getName() : "未知部门";
                departmentQueryWrapper.clear();
            }

            log.debug("队长ID {} 姓名 {} 部门名称 {}", userId, leaderName, departmentName);

            rowData.add(userId);
            rowData.add(leaderName);
            rowData.add(departmentName);

            // 固定列：项目组别，从work的schemaContent字段解析
            String workType = null;
            if (work.getSchemaContent() != null) {
                workType = getWorkType(work);
            }
            rowData.add(workType);

            log.debug("队长ID {} 作品ID {} 组别 {}", userId, work.getId(), workType);

            // 动态列：评委信息
            for (Score score : workScores) {
                String judgeCode = null;
                if (score.getJudgeId() != null) {
                    userQueryWrapper.eq("id", score.getJudgeId()).select("code");
                    User judge = userMapper.selectOne(userQueryWrapper);
                    if (judge != null) {
                        judgeCode = judge.getCode();
                    }
                    userQueryWrapper.clear();
                }

                rowData.add(judgeCode);
                rowData.add(score.getScore() != null ? score.getScore() : null);
                rowData.add(score.getOption() != null ? score.getOption() : null);

                log.debug(
                        "队长ID {} 作品ID {} 评委ID {} 工号 {} 评分 {} 意见 {}",
                        userId,
                        work.getId(),
                        score.getJudgeId(),
                        judgeCode,
                        score.getScore(),
                        score.getOption());
            }

            // 补齐空缺的评委列
            int currentJudgeNum = workScores.size();
            int lackJudgeNum = maxJudgeNum - currentJudgeNum;
            for (int i = 0; i < lackJudgeNum; i++) {
                rowData.add(""); // 评委工号
                rowData.add(""); // 评委评分
                rowData.add(""); // 评委意见
            }

            excelData.add(rowData);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("maxJudgeNum", maxJudgeNum);
        resultMap.put("excelData", excelData);
        return resultMap;
    }

    private String getWorkType(Work work) {
        try {
            JsonNode node = objectMapper.readTree(work.getSchemaContent());
            log.debug("作品ID {} schemaContent 解析成功", work.getId());

            for (JsonNode item : node) {
                if ("项目组别".equals(item.get("input").asText())) {
                    log.debug("作品ID {} 找到项目组别字段", work.getId());

                    return item.get("content").asText() != null
                            ? item.get("content").asText()
                            : null;
                }
            }

        } catch (IOException e) {
            log.error("作品ID {} 组别解析失败: {}", work.getId(), e.getMessage());
            return null;
        }
        return null;
    }
}
