package com.vale.valechat.model.dto.message;

import lombok.Data;

@Data
public class ChannelMessageRequest {

    /**
     * Sender id
     */
    private Long senderId;

    /**
     * Channel id
     */
    private Long channelId;

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
