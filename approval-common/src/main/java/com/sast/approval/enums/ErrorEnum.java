package com.sast.approval.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorEnum {
    COMMON_ERROR(1000, "错误"),
    TOKEN_ERROR(1001, "TOKEN错误"),
    NO_LOGIN(1002, "没有登录"),
    EXPIRED_LOGIN(1003, "登录过期"),
    NO_ROLE(1004,"无权限"),
    NO_TOKEN(1005,"TOKEN不能为空"),
    Login_ERROR(1006, "账号或者密码错误"),
    UNKNOWN_COMPETITION_ID(2001, "找不到相应的比赛"),
    UNKNOWN_TEAM_ID(2002, "找不到相应的队伍"),
    HAVE_NOT_SIGNED_COM(2003, "您还未报名该比赛"),
    HAVE_NOT_UPLOAD_WORK(2004, "您还未上传作品"),
    INVALID_FILE_TYPE_ERROR(3001, "文件类型不合法"),
    OSS_FAILED_UPLOAD_ERROR(3002, "文件上传至存储服务器时出错"),
    OSS_FAILED_DOWNLOAD_ERROR(3003, "文件下载至服务器时出错"),
    OSS_FAILED_DELETE_ERROR(3004, "删除存储服务器上的文件时出错"),
    OSS_BUCKET_NOT_EXIST(3005, "Bucket不存在"),
    OSS_FILE_NOT_EXIST(3006, "文件不存在"),
    INVALID_URL_ERROR(3007, "URL格式不合法"),
    SCORE_NOT_EXIST(4001, "评审结果不存在"),
    NO_RESULT(5002,"没有结果"),
    CONTEST_NOT_EXIST(6001, "比赛不存在"),
    CONTEST_ERROR(6002, "比赛操作失败"),
    DATE_ERROR(6003,"时间设置错误"),
    DEP_NOT_EXIST(6004, "该部门不存在"),
    LIMIT_ERROR(6005, "团队人数设置错误"),
    REVIEW_SETTINGS_ERROR(6006, "评审关系设置错误"),
    SCHEMA_ERROR(6007, "表单未设置"),
    WORK_NOT_EXIST(6008, "作品不存在"),
    ASSIGN_ERROR(6009, "无法分配评委，存在未审批或审批未通过的作品"),
    USER_NOT_EXIST(7001, "用户不存在"),
    FILE_NOT_EXIST(8001, "文件不存在"),
    FILE_EXPIRED_ERROR(8002, "文件已过期，请重新提交"),
    NOTICE_ERROR(9001, "公告发布失败"),
    NOTICE_NOT_EXIST(9002, "公告不存在"),
    IMPORT_ERROR(10000, "导入失败");

    private final Integer errCode;
    private final String errMsg;
}