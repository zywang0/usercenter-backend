package com.usercenter.usercenterbackend.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 头像URL
     */
    private String avatar;

    private Integer gender;

    /**
     * 用户密码
     */
    private String userPassword;

    private String phoneNum;

    private String email;

    /**
     * 0 - 正常
     */
    private Integer userStatus;

    private Date createTime;

    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 用户角色：User - 0, Admin - 1
     */
    private Integer role;

    /**
     * 星球编号
     */
    private String number;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}