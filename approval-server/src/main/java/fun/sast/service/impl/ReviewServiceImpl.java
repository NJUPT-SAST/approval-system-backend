package fun.sast.service.impl;

import com.alibaba.excel.EasyExcel;
import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.ReviewMapper;
import fun.sast.service.ReviewService;
import fun.sast.vo.ReviewExportVO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired private ReviewMapper reviewMapper;

    @Override
    public void exportReviewResult(Integer comId, HttpServletResponse response) {
        try {
            if (comId == null) {
                throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
            }
            // 设置响应头
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("评审结果", StandardCharsets.UTF_8);
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

            // 获取数据
            List<ReviewExportVO> reviewList = reviewMapper.selectReviewsForExport(comId);
            if (reviewList == null || reviewList.isEmpty()) {
                throw new BaseException(ErrorEnum.SCORE_NOT_EXIST);
            }

            // 导出数据
            EasyExcel.write(response.getOutputStream(), ReviewExportVO.class)
                    .sheet("评审结果")
                    .doWrite(reviewList);

        } catch (IOException e) {
            throw new BaseException(ErrorEnum.OSS_FAILED_DOWNLOAD_ERROR);
        } finally {
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
