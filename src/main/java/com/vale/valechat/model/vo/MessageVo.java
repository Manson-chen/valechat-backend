package com.vale.valechat.model.vo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class MessageVo implements Serializable {
    private static final long serialVersionUID = 5939459225549459049L;
    private Long messageId;

    private Long senderId;

    private String senderName;

    private Long channelId;

    private String content;

    private Date sendTime;

    private Integer hasRead;
}
