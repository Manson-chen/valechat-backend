package com.vale.valechat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName organization
 */
@TableName(value ="organization")
@Data
public class Organization implements Serializable {
    /**
     * organization id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * organization name
     */
    private String organizationName;

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

    /**
     * email address
     */
    private String email;

    /**
     * organization avatar url
     */
    private String OrganizationAvatar;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}