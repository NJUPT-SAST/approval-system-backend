package fun.sast.controller.adminController;

import com.alibaba.fastjson2.JSONObject;
import fun.sast.annotation.CheckRole;
import fun.sast.annotation.ResponseResult;
import fun.sast.enums.UserRoleEnum;
import fun.sast.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CompetitionController {
    private final CompetitionService competitionService;

    @ResponseResult
    @GetMapping("/admin/com/schema")
    public JSONObject getSchema(@RequestParam Long comId) {
        return competitionService.getSchema(comId);
    }
}
