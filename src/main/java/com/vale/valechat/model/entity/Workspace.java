package com.vale.valechat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName workspace
 */
@TableName(value ="workspace")
@Data
public class Workspace implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * workspace name
     */
    private String workspaceName;

    /**
     * the id of the user who created this workspace.
     */
    private Long masterId;

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