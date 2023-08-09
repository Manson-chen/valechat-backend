package com.vale.valechat.model.vo;

import com.vale.valechat.model.entity.PrivateFile;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CommonMessageVO implements Serializable {

    private Long id;

    /**
     * corresponding workspace
     */
    private Long workspaceId;

    /**
     * sender id
     */
    private Long senderId;

    /**
     * receiver id
     */
    private Long receiverId;

    /**
     * Message type
     */
    private Integer msgType;

    /**
     * Message content (non-text display url)
     */
    private String content;

    /**
     * Creation date
     */
    private Date createTime;

    private List<FileListVO> visibleFileList;

    private static final long serialVersionUID = 1925318970029265222L;
}
