package com.sast.approval.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Judge implements Serializable {
    /**
     * 活动id
     */
    private long comId;
    /**
     * 评委id
     */
    private Integer id;
    /**
     * 授权人id
     */
    private Integer userId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
