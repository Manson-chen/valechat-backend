package com.vale.valechat.model.dto.message;

import com.vale.valechat.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelSearchRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 3394470067660778211L;

    private long chatId;

    private Integer chatType;

    private Long currentUserId;

    private long workspaceId;

    private String searchWord;
}
