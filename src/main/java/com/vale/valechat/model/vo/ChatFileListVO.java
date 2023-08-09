package com.vale.valechat.model.vo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Data
public class ChatFileListVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * The message corresponding to this file (the file is sent in chat)
     */
    private Long messageId;

    private Long senderId;

    private String senderName;

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
     * Creation date
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
