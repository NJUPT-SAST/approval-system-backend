package fun.sast.controller.adminController;

import fun.sast.Exception.BaseException;
import fun.sast.annotation.ResponseResult;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.service.WhiteListService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/com")
@RequiredArgsConstructor
public class WhiteListController {
    private final WhiteListService whiteListService;

    @ResponseResult
    @PostMapping("/whitelist")
    public void setWhiteList(
            @RequestParam Long comId,
            @RequestParam Boolean isEnable,
            @RequestParam(required = false) MultipartFile excelFile) {
        User currentUser = UserInterceptor.userHolder.get();
        if (currentUser.getRole() != 3) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }
        whiteListService.operateWhiteList(comId, isEnable, excelFile);
    }
}
