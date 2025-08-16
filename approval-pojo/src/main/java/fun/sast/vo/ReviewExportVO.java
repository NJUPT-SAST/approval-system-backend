package fun.sast.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewExportVO {
    @ExcelProperty("评审结果ID")
    private String id;

    @ExcelProperty("评委ID")
    private Integer judgeId;

    @ExcelProperty("队长ID")
    private String userId;

    @ExcelProperty("比赛ID")
    private Integer comId;

    @ExcelProperty("评分")
    private Integer score;

    @ExcelProperty("是否通过")
    private boolean accept;

    @ExcelProperty("评审意见")
    private String option;

    @ExcelProperty("创建时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ExcelProperty("更新时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @ExcelProperty("创建人ID")
    private Long createUser;
    @ExcelProperty("更新人ID")
    private Long updateUser;
}
