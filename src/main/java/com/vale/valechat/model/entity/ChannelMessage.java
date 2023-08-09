package com.vale.valechat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName channel_message
 */
@TableName(value ="channel_message")
@Data
public class ChannelMessage implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Message content
     */
    private String content;

    /**
     * Read status(0-unread, 1-read)
     */
    private Integer isRead;

    /**
     * sender id
     */
    private Long senderId;

    /**
     * receiver id
     */
    private Long channelId;

    /**
     * corresponding workspace
     */
    private Long workspaceId;

    /**
     * delete status (0-not deleted, 1-deleted)
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * Creation date
     */
    private Date createTime;

    /**
     * update date
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}