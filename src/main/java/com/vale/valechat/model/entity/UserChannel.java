package com.vale.valechat.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;

/**
 * 
 * @TableName user_channel
 */
@TableName(value ="user_channel")
@Data
public class UserChannel implements Serializable {

    @MppMultiId
    @TableField(value = "user_id")
    private Long userId;

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