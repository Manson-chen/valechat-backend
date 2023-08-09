package com.vale.valechat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName user_star
 */
@TableName(value ="user_star")
@Data
public class UserStar implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * The user(id) who create the star
     */
    private Long userId;

    /**
     * 3 Types: 0-user, 1-channel, 2-orgChannel
     */
    private Integer starType;

    /**
     * If star_type=0, starred_id=user_id, otherwise channel_id
     */
    private Long starredId;

    private Long workspaceId;

    /**
     * The time star
     */
    private Date createTime;

    /**
     * update date
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}