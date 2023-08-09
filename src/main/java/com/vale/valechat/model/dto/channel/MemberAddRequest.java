package com.vale.valechat.model.dto.channel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MemberAddRequest implements Serializable {
    private static final long serialVersionUID = 3394470067660778211L;
    public long channelId;
    public List<Long> organizationIdList;
    public List<Long> memberIdList;
}
