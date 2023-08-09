package com.vale.valechat.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrgChannelVo implements Serializable {
    private static final long serialVersionUID = 2359857568312725388L;
    private Long id;
    private String chatName;
    private long channelId;
    private String chatEmail;
    private String chatAvatar;
    private Integer channelType;
}
