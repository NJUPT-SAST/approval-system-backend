package fun.sast.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/data")
@RequiredArgsConstructor
public class ExportFileController {
    //    private final FileService fileService;
    //
    //    @GetMapping("/exportComInfo")
    //    public void exportComInfo(HttpServletResponse response, @RequestParam Long comId) {
    //        User currentUser = UserInterceptor.userHolder.get();
    //        if (currentUser.getRole() != 3) {
    //            response.setContentType("application/json;charset=utf-8");
    //            try {
    //                response.getWriter().write("{\"code\":403,\"msg\":\"无权限\"}");
    //            } catch (IOException e) {
    //                throw new RuntimeException(e);
    //            }
    //            throw new BaseException(ErrorEnum.NO_ROLE);
    //        }
    //        try {
    //            fileService.exportComInfo(response, comId);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            response.setContentType("application/json;charset=utf-8");
    //            try {
    //                response.getWriter().write("{\"code\":500,\"msg\":\"导出参赛信息失败\"}");
    //            } catch (IOException ex) {
    //                throw new RuntimeException(ex);
    //            }
    //            throw new BaseException(ErrorEnum.EXPORT_COMINFO_ERROR);
    //        }
    //    }
    //
    //    @ResponseResult
    //    @GetMapping("/exportWork")
    //    public void exportWork(
    //            HttpServletResponse response, @RequestParam Long comId, @RequestParam String
    // userCode) {
    //        User currentUser = UserInterceptor.userHolder.get();
    //        if (currentUser.getRole() != 3) {
    //            throw new BaseException(ErrorEnum.NO_ROLE);
    //        }
    //        fileService.exportWork(response, comId, userCode);
    //    }
}
