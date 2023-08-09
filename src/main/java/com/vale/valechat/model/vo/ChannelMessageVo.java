package com.vale.valechat.model.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class ChannelMessageVo implements Serializable {
    private static final long serialVersionUID = -3251888658233655534L;
    private Long id;

    private Integer type;

    private String content;

    private Integer isRead;

    private Long senderId;

    private String senderName;

    private Long channelId;

    private Long workspaceId;

    private Integer isDeleted;

    private Date createTime;

    private Date updateTime;
}
