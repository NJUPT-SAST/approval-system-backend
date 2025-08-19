package fun.sast.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FileUploadCache implements Serializable {

    /** 竞赛ID */
    private Long comId;

    /** 用户学号 */
    private String userCode;

    /** 输入框名称 */
    private String input;

    /** 文件URL */
    private String url;

    /** 创建时间 */
    private LocalDateTime date;

    public File toFile() {
        File file = new File();
        file.setComId(comId);
        file.setUserCode(userCode);
        file.setInput(input);
        file.setUrl(url);
        return file;
    }
}
