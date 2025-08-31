package fun.sast.controller.publicController;

import com.sun.net.httpserver.Authenticator;
import fun.sast.response.GlobalResponse;
import fun.sast.service.CompetitionService;
import fun.sast.service.impl.CompetitionServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CompetitionController {
    private final CompetitionService competitionService;

    @GetMapping("/review/red-point")
    public Object reviewPoint(){
        return GlobalResponse.success(competitionService.reviewPoint());
    }


}
