package fun.sast.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class WorkSchemaDTO implements Serializable {
    private String input;
    private String content;
    private Boolean isFile;
}
