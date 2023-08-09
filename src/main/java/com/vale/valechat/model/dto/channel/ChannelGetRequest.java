package com.vale.valechat.model.dto.channel;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelGetRequest  implements Serializable {

    private static final long serialVersionUID = 3394470067660778211L;

    public long userId;

    public long workspaceId;
}