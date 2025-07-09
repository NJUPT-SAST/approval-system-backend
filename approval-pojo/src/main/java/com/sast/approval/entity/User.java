package com.sast.approval.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    //TODO 这什么意思
    private Department depId;
    /**
     * 用户id编号
     */
    private Integer id;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 用户学号
     */
    private String num;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 角色
     */
    private long role;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
