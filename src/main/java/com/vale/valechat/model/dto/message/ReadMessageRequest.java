package com.vale.valechat.model.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ReadMessageRequest implements Serializable {

    private Long userId;

    /**
     * if msgType == 0, senderId = senderId, else senderId = channelId
     */
    private Long readChatId;

    private Integer msgType;

    private static final long serialVersionUID = 1L;
}
