package com.vale.valechat.model.dto.channel;

import lombok.Data;

@Data
public class ChannelEditRequest {
    private static final long serialVersionUID = 3394470067660778211L;

    public long id;

    public long workspaceId;

    public String channelName;
}
