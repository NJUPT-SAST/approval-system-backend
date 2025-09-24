package fun.sast.service.impl;

import static com.baomidou.mybatisplus.core.toolkit.IdWorker.getId;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.*;
import fun.sast.enums.ErrorEnum;
import fun.sast.enums.UserRoleEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.mapper.*;
import fun.sast.service.FileService;
import fun.sast.utils.COSUtil;
import fun.sast.utils.FileUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {


    private final FileMapper fileMapper;
    private final COSUtil cosUtil;
    private final FileUtil fileUtil;
    private final CompetitionMapper competitionMapper;
    private final WorkMapper workMapper;
    private final TeamMapper teamMapper;
    private final UserMapper userMapper;

    /**
     * @param url 文件存储的url，如http://baiyaoshi.oss-cn-hangzhou.aliyuncs.com/文本.txt,在这里实现身份判断
     * @return 可以直接用于下载的凭证
     */
    @Override
    public String getDownloadCertificate(String url) {

        // 获取user身份信息
        User user = UserInterceptor.userHolder.get();

        if (user == null) {
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }

        // 解码
        url = URLDecoder.decode(url, StandardCharsets.UTF_8);

        // 判断url是否合法
        if (!cosUtil.isLegalCOSUrl(url)) {
            throw new BaseException(ErrorEnum.INVALID_URL_ERROR);
        }
        String cosCert = cosUtil.getDownloadCertificate(url);
        // 提取 objectKey 对比数据库看有没有文件
        String objectKey = cosUtil.extractObjectKey(cosCert);
        if (objectKey == null) {
            throw new BaseException(ErrorEnum.INVALID_URL_ERROR);
        }

        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", objectKey);
        File file = fileMapper.selectOne(queryWrapper);

        if (file == null) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }

        // 权限控制
        if (user.getRole().equals(UserRoleEnum.TOURIST.getRole())) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }

        if (user.getRole().equals(UserRoleEnum.STUDENT.getRole())
                && !user.getCode().equals(file.getUserCode())) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }

        return cosUtil.getDownloadCertificate(url);
    }

    /**
     * 导出比赛参赛信息
     *
     * @param comId
     */
    @Override
    public File exportComInfo(HttpServletResponse response,Long comId) {
        try {

            // 查询比赛信息
            Competition competition = competitionMapper.selectById(comId);
            if (competition == null) {
                log.error("比赛不存在，comId={}", comId);
                throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
            }
            int maxTeamMembers = competition.getMaxTeamMembers();
            log.info("比赛{}最大队伍人数为{}", comId, maxTeamMembers);
            // 构建Excel表头和数据
            List<List<String>> head = buildExcelHead(maxTeamMembers);
            List<List<String>> dataList = dataList(comId, maxTeamMembers,competition);

            // 写入Excel并响应
            EasyExcel.write(response.getOutputStream())
                    .head(head)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("参赛信息")
                    .doWrite(dataList);
            log.info("导出比赛参赛信息成功, comId={}, 参赛队伍数={}", comId, dataList.size());
        } catch (IOException e) {
            log.error("导出比赛参赛信息失败: {}", e.getMessage());
            throw new BaseException(ErrorEnum.EXPORT_COMINFO_ERROR);
        }
        return null;
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
        log.info("Excel表头构建完成，最大队伍人数为{}，总列数为{}", maxTeamMembers, head.size());
        return head;
    }

    private List<List<String>> dataList(Long comId, int maxTeamMembers,Competition competition) {
        List<List<String>> dataList = new ArrayList<>();

        // 查询该比赛所有队伍
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("com_id", comId);
        List<Team> teams = teamMapper.selectList(teamQueryWrapper);
        log.info("查询比赛{}的参赛队伍，共{}支队伍", comId, teams.size());

        if (CollectionUtils.isEmpty(teams)) {
            return dataList;
        }

        // 遍历队伍，查询每个队伍的作品和成员信息
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            List<String> dataRow = new ArrayList<>();

            // 固定列数据
            dataRow.add(String.valueOf(competition.getName())); // 列1：比赛名称
            dataRow.add(String.valueOf(getId())); // 列2：队伍ID
            dataRow.add(String.valueOf(team.getName())); // 列3：队伍名称
            dataRow.add(String.valueOf(team.getTeacher())); // 列4：指导老师

            String captainCode = team.getCaptain();
            User captain = null;
            if (StringUtils.hasText(captainCode)) {
                captain = userMapper.selectOne(new QueryWrapper<User>().eq("code", captainCode));
            }
            dataRow.add(captain != null ? captain.getCode() : "无"); // 列5：队长学号
            log.info("队伍{}的队长学号为{}", team.getId(), captain != null ? captain.getCode() : "无");

            Work work = workMapper.selectOne(new QueryWrapper<Work>().eq("com_id", competition.getId()));
            dataRow.add(work != null ? String.valueOf(work.getId()) : "无"); // 列6：作品ID
            dataRow.add(work != null ? work.getWorkName() : "无"); // 列7：作品名称
            log.info("队伍{}的作品ID为{}", team.getId(), work != null ? work.getId() : "无");

            // 解析队员和指导老师字段
            List<Map<String, String>> members = parseMembers(team.getMember());
            for (int j = 0; j < maxTeamMembers - 1; j++) {
                if (members != null && j < members.size()) {
                    Map<String, String> member = members.get(j);
                    dataRow.add(member.get("code")); // 列8：队员学号
                    dataRow.add(member.get("name")); // 列9：队员姓名
                } else {
                    dataRow.add("无");
                    dataRow.add("无");
                }
            }
            log.info("队伍{}的成员数为{}", team.getId(), members != null ? members.size() : 0);

            List<Map<String, String>> teachers = parseTeachers(team.getTeacher());
            if(CollectionUtils.isEmpty(teachers)){
               dataRow.add("无");
               log.debug("队伍{}的指导老师信息为空", team.getId());
            }else{
                String teacherNames = String.join(", ", teachers.stream().map(t -> t.getOrDefault("name", "无")).collect(Collectors.joining(", ")));
                dataRow.add(teacherNames); // 列10：指导老师
                log.debug("队伍{}的指导老师信息为{}", team.getId(), teacherNames);
            }
            log.info("队伍{}的指导老师数为{}", team.getId(), teachers != null ? teachers.size() : 0);
        }
        return dataList;
    }

    private List<Map<String, String>> parseMembers(String memberJson) {
        List<Map<String, String>> members = new ArrayList<>();
        if (memberJson == null || memberJson.isEmpty()) {
            return null;
        }
        try {
            JSONArray jsonArray = JSONArray.parseArray(memberJson);
            for (Object obj : jsonArray) {
                JSONObject json = (JSONObject) obj;
                Map<String, String> memberInfo =
                        Map.of("code", json.getString("code"), "name", json.getString("name"));
                members.add(memberInfo);
            }
            log.info("解析成员信息成功，共{}名成员", members.size());
        } catch (Exception e) {
            log.error("解析成员信息失败: {}", e.getMessage());
            return null;
        }
        return members;
    }

    private List<Map<String, String>> parseTeachers(String teacherJson) {
        List<Map<String, String>> teachers = new ArrayList<>();
        if (teacherJson == null || teacherJson.isEmpty()) {
            log.error("指导老师信息为空");
            return teachers;
        }

        try {
            JSONArray jsonArray = JSONArray.parseArray(teacherJson);
            log.debug("指导老师JSON数组: {}", jsonArray.toJSONString());
            for (Object obj : jsonArray) {
                JSONObject json = (JSONObject) obj;
                Map<String, String> teacherInfo = Map.of("name", json.getString("name"));
                teachers.add(teacherInfo);
            }
            log.info("解析指导老师信息成功，共{}名指导老师", teachers.size());
        } catch (Exception e) {
            log.error("解析指导老师信息失败: {}", e.getMessage());
            return teachers;
        }
        return teachers;
    }

    @Override
    public File exportWork(HttpServletResponse response, Long comId, String userCode) {
        Competition competition = competitionMapper.selectById(comId);
        if (competition == null) {
            throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
        }
        String competitionName = competition.getName();

        Work work = workMapper.selectOne(
                        new QueryWrapper<Work>().eq("com_id", comId).eq("user_code", userCode));
        if (work == null) {
            throw new BaseException(ErrorEnum.WORK_NOT_EXIST);
        }
        String workName = work.getWorkName();

        // 校验作品是否有文件
        List<File> files =
                fileMapper.selectList(new QueryWrapper<File>().eq("work_id", work.getId()));
        if (CollectionUtils.isEmpty(files)) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }

        // 组装文件信息
        List<Map<String, String>> fileInfoList = new ArrayList<>();
        for (File file : files) {
            Map<String, String> fileInfo = new HashMap<>();
            // 文件名优先用input字段，没有则用cos地址提取的文件名
            String fileName =
                    StringUtils.hasText(file.getInput())
                            ? file.getInput()
                            : FileUtil.getFileName(file.getUrl());
            fileInfo.put("fileName", fileName);
            fileInfo.put("cosUrl", file.getUrl()); // 附件原始的cos地址
            fileInfoList.add(fileInfo);
        }

        // 生成zip文件名（比赛名称-作品名称-队长学号.zip）
        String zipFileName = competitionName + "-" + workName + ".zip";

        // 打包下载
        fileUtil.downloadPackFile(response, fileInfoList, zipFileName);
        return null;
    }
}
