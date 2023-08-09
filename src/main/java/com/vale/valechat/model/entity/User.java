package com.vale.valechat.model.entity;

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
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * user nickname
     */
    private String userName;

    /**
     * user account
     */
    private String userAccount;

    /**
     * password
     */
    private String userPassword;

    /**
     * gender (0-male, 1-female)
     */
    private Integer gender;

    /**
     * phone number
     */
    private String phone;

    /**
     * email address
     */
    private String email;

    /**
     * user avatar url
     */
    private String userAvatar;

    /**
     * user login status (0-offline, 1-online)
     */
    private Integer userStatus;

    /**
     * user role (0-common User, 1-institute)
     */
    private Integer userRole;

    /**
     * Referenced organization_id in organization
     */
    private Long organizationId;

    /**
     * delete status (0-not deleted, 1-deleted)
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * creation date
     */
    private Date createTime;

    /**
     * update date
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}