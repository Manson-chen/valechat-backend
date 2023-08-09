package com.vale.valechat.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName user_workspace
 */
@TableName(value ="user_workspace")
@Data
public class UserWorkspace implements Serializable {

    @MppMultiId
    @TableField("user_id")
    private Long userId;

    @MppMultiId
    @TableField("workspace_id")
    private Long workspaceId;

    /**
     * The time when the user joined the workspace
     */
    private Date joinTime;

    /**
     * delete status (0-not deleted, 1-deleted)
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}