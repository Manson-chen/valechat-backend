package com.vale.valechat.model.dto.channel;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDeleteRequest implements Serializable {
    private static final long serialVersionUID = 3394470067660778211L;
    public long channelId;
    public long userId;
}
