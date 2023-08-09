package com.vale.valechat.model.dto.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrivateMessageRequest implements Serializable {

    /**
     * sender id
     */
    private Long senderId;

    /**
     * receiver id
     */
    private Long receiverId;

    /**
     * Message content (non-text display url)
     */
    private String content;

    /**
     * corresponding workspace
     */
    private Long workspaceId;



    private static final long serialVersionUID = 1L;
}
