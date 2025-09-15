package fun.sast.vo;

import fun.sast.entity.Department;
import lombok.Data;

@Data
public class UserInfoVO {
    private int id;
    private String name;
    private String num;
    private Department dep_id;
    private int role;
}
