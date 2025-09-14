package fun.sast.controller.userController;

import com.alibaba.excel.EasyExcel;
import fun.sast.annotation.ResponseResult;
import fun.sast.service.AdminService;
import fun.sast.service.FileService;
import fun.sast.utils.ExcelForJudgeAssignUtil;
import fun.sast.vo.CompetitionListVO;
import fun.sast.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final FileService fileService;
    private final ExcelForJudgeAssignUtil excelForJudgeAssignUtil;

    @ResponseResult
    @GetMapping("com/competitionList")
    public CompetitionListVO getCompetitionList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return adminService.getCompetitionList(pageNum, pageSize);
    }

    @ResponseResult
    @GetMapping("/userInfo")
    public UserInfoVO getUserInfo(String code) {
        return adminService.getUserInfo(code);
    }

    @GetMapping("/exportWorkData")
    public void exportWorkData(HttpServletResponse response, @RequestParam long comId) {
        // 设置响应头
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        // 格式化时间戳
        String timeStr =
                new java.text.SimpleDateFormat("yyyy-MM-dd_HHmm").format(new java.util.Date());
        String fileName =
                URLEncoder.encode("比赛" + comId + "作品id及名称_" + timeStr, StandardCharsets.UTF_8)
                        .replaceAll("\\+", "%20");
        response.setHeader(
                "Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        fileService.exportWorkExcel(comId, response);
    }

    @ResponseResult
    @PostMapping("/judge/assign")
    public Map<Long, List<String>> assignJudges(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), excelForJudgeAssignUtil).sheet().doRead();
            return excelForJudgeAssignUtil.getWorkJudgeMap();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
