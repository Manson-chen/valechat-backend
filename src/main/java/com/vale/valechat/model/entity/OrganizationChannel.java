package com.vale.valechat.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName organization_channel
 */
@TableName(value ="organization_channel")
@Data
public class OrganizationChannel implements Serializable {
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
    @TableField(value = "channel_id")
    private Long channelId;

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