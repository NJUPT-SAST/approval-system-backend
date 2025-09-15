package fun.sast.controller;

import com.alibaba.fastjson2.JSON;
import fun.sast.annotation.ResponseResult;
import fun.sast.entity.Competition;
import fun.sast.service.AdminService;
import fun.sast.vo.CompetitionDetailVO;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 创建活动
     *
     * @param competition 比赛（活动）
     * @param cover 封面
     * @return 比赛id
     */
    @PostMapping("/com/create")
    @ResponseResult
    public Long createCompetition(@RequestParam String competition, MultipartFile cover) {
        Competition parseCom = JSON.parseObject(competition, Competition.class);
        adminService.createCompetition(parseCom, cover);
        return parseCom.getId();
    }

    /**
     * 修改活动信息
     *
     * @param competition 活动（比赛）
     * @return 活动id
     */
    @PostMapping("/com/edit")
    public Long editCompetition(@RequestParam String competition, MultipartFile cover) {
        Competition parseCom = JSON.parseObject(competition, Competition.class);
        adminService.editCompetition(parseCom, cover);
        return parseCom.getId();
    }

    /**
     * 删除活动
     *
     * @param comId 活动id
     * @return 执行结果
     */
    @PostMapping("/com/delete")
    public String deleteCompetition(@RequestParam Long comId) {
        adminService.deleteCompetition(comId);
        return "success";
    }

    /**
     * 获取活动信息
     *
     * @param comId 活动id
     * @return 活动信息
     */
    @GetMapping("/com/competitionInfo")
    public CompetitionDetailVO getCompetitionInfo(@RequestParam Long comId) {
        return adminService.getCompetitionInfo(comId);
    }

    /**
     * 管理活动
     *
     * @param comId 活动id
     * @param pageNum 当前页数
     * @param pageSize 每页大小
     * @return 活动管理界面的信息
     */
    @GetMapping("/com/manager")
    public Map<String, Object> comManager(
            @RequestParam Long comId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return adminService.getComMangerInfo(pageNum, pageSize, comId);
    }
}
