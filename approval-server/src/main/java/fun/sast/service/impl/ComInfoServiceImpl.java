package fun.sast.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.Competition;
import fun.sast.entity.Team;
import fun.sast.entity.Work;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.mapper.TeamMapper;
import fun.sast.mapper.UserMapper;
import fun.sast.mapper.WorkMapper;
import fun.sast.service.ComInfoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.baomidou.mybatisplus.core.toolkit.IdWorker.getId;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComInfoServiceImpl implements ComInfoService {
    private final CompetitionMapper competitionMapper;
    private final WorkMapper workMapper;
    private final TeamMapper teamMapper;
    private final UserMapper userMapper;

    @Override
    public void exportComInfo(HttpServletResponse response, Long comId) {
        try {
            //设置响应格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("参赛信息_" + comId + "_" + System.currentTimeMillis(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            //查询比赛信息
            Competition competition = competitionMapper.selectById(comId);
            if (competition == null) {
                throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
            }
            int maxTeamMembers = competition.getMaxTeamMembers();
            //构建Excel表头和数据
            List<List<String>> head = buildExcelHead(maxTeamMembers);
            List<List<String>> dataList = dataList(comId, maxTeamMembers);

            //写入Excel并响应
            EasyExcel.write(response.getOutputStream())
                    .head(head)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("参赛信息")
                    .doWrite(dataList);
        } catch (IOException e) {
            throw new BaseException(ErrorEnum.EXPORT_COMINFO_ERROR);
        }
    }

    private List<List<String>> buildExcelHead(int maxTeamMembers) {
        List<List<String>> head = new ArrayList<>();
        head.add(List.of("比赛名称"));
        head.add(List.of("作品ID"));
        head.add(List.of("作品名称"));
        head.add(List.of("队伍ID"));
        head.add(List.of("队伍名称"));
        head.add(List.of("队长名字"));

        for (int i = 1; i < maxTeamMembers; i++) {
            head.add(List.of("队员" + i + "学号"));
            head.add(List.of("队员" + i + "姓名"));
        }

        head.add(List.of("指导老师"));
        return head;
    }

    private List<List<String>> dataList(Long comId, int maxTeamMembers) {
        List<List<String>> dataList = new ArrayList<>();

        //查询该比赛所有队伍
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("com_id", comId);
        List<Team> teams = teamMapper.selectList(teamQueryWrapper);

        if (CollectionUtils.isEmpty(teams)) {
            return dataList;
        }

        //遍历队伍，查询每个队伍的作品和成员信息
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            List<String> dataRow = new ArrayList<>();

            //固定列数据
            dataRow.add(String.valueOf(getId()));// 队伍ID
            dataRow.add(String.valueOf(team.getName())); // 队伍名称
            dataRow.add(String.valueOf(team.getTeacher())); // 指导老师

            String captainCode = team.getCaptain();// 队长学号
            dataRow.add(captainCode);
            String captainName = userMapper.selectById(captainCode).getName();
            dataRow.add(captainName);

            Work work = workMapper.selectOne(new QueryWrapper<Work>().eq("team_id", team.getId()));
            dataRow.add(work != null ? String.valueOf(work.getId()) : "无");
            dataRow.add(work != null ? work.getWorkName() : "无");

            //解析队员和指导老师字段
            List<Map<String, String>> members = parseMembers(team.getMember());
            for(int j = 0;j < maxTeamMembers - 1;j++) {
                if(members != null && j < members.size()) {
                    Map<String, String> member = members.get(j);
                    dataRow.add(member.get("code"));
                    dataRow.add(member.get("name"));
                } else {
                    dataRow.add("无");
                    dataRow.add("无");
                }
            }
            List<Map<String, String>> teachers = parseTeachers(team.getTeacher());
            for(int j = 0;j < teachers.size();j++) {
                if(teachers != null ) {
                    Map<String, String> teacher = teachers.get(j);
                    dataRow.add(teacher.get("dep_id"));
                    dataRow.add(teacher.get("name"));
                } else {
                    dataRow.add("无");
                    dataRow.add("无");
                }
            }
        }
        return dataList;
    }

    private List<Map<String, String>> parseMembers(String memberJson) {
        List<Map<String, String>> members = new ArrayList<>();
        if(memberJson != null && !memberJson.isEmpty()) {
            return null;
        }
        try{
            JSONArray jsonArray = JSONArray.parseArray(memberJson);
            for(Object obj: jsonArray) {
                JSONObject json = (JSONObject) obj;
                Map<String, String> memberInfo = Map.of("code", json.getString("code"), "name", json.getString("name"));
                members.add(memberInfo);
            }
        }catch (Exception e){
            log.error("解析成员信息失败: {}", e.getMessage());
            return null;
        }
        return members;
    }

    private List<Map<String,String>> parseTeachers(String teacherJson) {
        List<Map<String,String>> teachers = new ArrayList<>();
        if(teacherJson == null || teacherJson.isEmpty()) {
            return null;
        }

        try{
            JSONArray jsonArray = JSONArray.parseArray(teacherJson);
            for(Object obj: jsonArray) {
                JSONObject json = (JSONObject) obj;
                Map<String, String> teacherInfo = Map.of(json.getString("name"), json.getString("dep_id"));
                teachers.add(teacherInfo);
            }
        }catch (Exception e){
            log.error("解析指导老师信息失败: {}", e.getMessage());
            return null;
        }
        return teachers;
    }
}

