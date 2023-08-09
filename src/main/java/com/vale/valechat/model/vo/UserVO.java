package com.vale.valechat.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 *
 * @TableName user
 */
@Data
public class UserVO implements Serializable {
    /**
     * id
     */
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
     * creation date
     */
    private Date createTime;

    /**
     * update date
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}