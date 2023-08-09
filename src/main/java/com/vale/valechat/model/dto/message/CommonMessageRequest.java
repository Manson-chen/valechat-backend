package com.vale.valechat.model.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class CommonMessageRequest implements Serializable {

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
    @Schema(description = "channel message - channelId, private message - receiver userId")
    private Long receiverId;

    /**
     * Message type
     */
    @Schema(description = "private message-0, channel message-1")
    private Integer msgType;

    /**
     * Message content (non-text display url)
     */
    private String content;

    @Size(max = 50 * 1024 * 1024, message = "Upload file size cannot exceed 50MB")
    private MultipartFile[] files;

    private static final long serialVersionUID = 1L;
}
