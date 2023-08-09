package com.vale.valechat.model.vo;

import com.vale.valechat.model.entity.Channel;
import com.vale.valechat.model.entity.User;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Display private chat(user list) and channel chat(channel list)
 */
@Data
public class ChatListVO implements Serializable {

    /**
     * Private chat user list
     */
    private List<UserVO> userList;

    /**
     * Channel chat chat list
     */
    private List<Channel> channelList;

    /**
     * private chat between User and Organization list
     */
    private List<OrgChannelVo> orgChannelList;

    private static final long serialVersionUID = -4062738799703155913L;
}
