package com.vale.valechat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName channel
 */
@TableName(value ="channel")
@Data
public class Channel implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Group name
     */
    private String channelName;

    /**
     * 
     */
    private Long workspaceId;

    /**
     * the id of the creator.
     */
    private Long masterId;

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

    private Integer ChannelType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}