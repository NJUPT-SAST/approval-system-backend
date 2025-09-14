package fun.sast.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class WorkOutPutVO {
    @ExcelProperty("作品ID")
    private Long id;

    @ExcelProperty("作品名称")
    private String name;
}
