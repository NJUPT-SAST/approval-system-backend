package fun.sast.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewExportVO {
    @ExcelProperty("评审ID")
    private String id;

    @ExcelProperty("评委ID")
    private Integer judgeId;

    @ExcelProperty("申请人ID")
    private String userId;

    @ExcelProperty("比赛ID")
    private Integer comId;

    @ExcelProperty("评分")
    private Integer score;

    @ExcelProperty("评审意见")
    private String option;
}
