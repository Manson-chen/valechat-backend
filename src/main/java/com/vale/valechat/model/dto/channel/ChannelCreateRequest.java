package com.vale.valechat.model.dto.channel;

import lombok.Data;

import java.io.Serializable;

/**
 * Group create request body
 *
 */
@Data
public class ChannelCreateRequest implements Serializable {

    private static final long serialVersionUID = 3394470067660778211L;

    public long masterId;

    public long workspaceId;

    public String channelName;
}