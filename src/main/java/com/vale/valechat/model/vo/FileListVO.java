package com.vale.valechat.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serializable;

@Data
public class FileListVO implements Serializable {
    private static final long serialVersionUID = -6847729368940078980L;
    /**
     * id
     */
    private Long id;

    /**
     * The message corresponding to this file (the file is sent in chat)
     */
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
     * File access url
     */
    private String fileUrl;
}
