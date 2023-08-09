package com.vale.valechat.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName organization_workspace
 */
@TableName(value ="organization_workspace")
@Data
public class OrganizationWorkspace implements Serializable {
    /**
     * 
     */
    @MppMultiId
    @TableField(value = "organization_id")
    private Long organizationId;

    /**
     * 
     */
    @MppMultiId
    @TableField(value = "workspace_id")
    private Long workspaceId;

    /**
     * The time when the user joined the workspace
     */
    private Date joinTime;

    /**
     * delete status (0-not deleted, 1-deleted)
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}