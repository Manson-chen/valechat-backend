package com.vale.valechat.model.dto.file;

import com.vale.valechat.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ChatFileRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 438874831935306286L;
    /**
     * corresponding workspace
     */
    @NotNull
    private Long workspaceId;

    /**
     * sender id
     */
    @NotNull
    private Long senderId;

    /**
     * receiver id
     */
    @NotNull
    @Schema(description = "channel message - channelId, private message - receiver userId")
    private Long receiverId;

    /**
     * File type
     */
    @NotNull
    @Schema(description = "private file-0, channel file-1")
    private Integer chatType;

    /**
     * File name
     */
    @NotNull
    private String fileName;
}
