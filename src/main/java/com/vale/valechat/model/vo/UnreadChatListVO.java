package com.vale.valechat.model.vo;

import com.vale.valechat.model.entity.Channel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UnreadChatListVO implements Serializable {

    private Long userId;

    /**
     * Private chat user list
     */
    private List<Long> userList;

    /**
     * Channel chat chat list
     */
    private List<Long> channelList;

    private static final long serialVersionUID = 1L;
}
