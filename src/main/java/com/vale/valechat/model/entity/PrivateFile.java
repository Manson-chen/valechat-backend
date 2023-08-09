package com.vale.valechat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName private_file
 */
@TableName(value ="private_file")
@Data
public class PrivateFile implements Serializable {
    /**
     * id
     */
    @MppMultiId
    @TableField("id")
    private Long id;

    /**
     * The message corresponding to this file (the file is sent in chat)
     */
    @MppMultiId
    @TableField("message_id")
    private Long messageId;

    /**
     * File type(0-file, 1-picture, 2-audio, 3-video)
     */
    private String fileType;

    /**
     * File name
     */
    private String fileName;

    /**
     * Unique file name
     */
    private String uniqueName;

    /**
     * File access address-url
     */
    private String fileUrl;

    /**
     * File storage address-url
     */
    private String filePath;

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