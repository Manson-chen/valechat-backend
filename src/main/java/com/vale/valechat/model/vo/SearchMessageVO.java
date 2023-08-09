package com.vale.valechat.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
@Data
public class SearchMessageVO implements Serializable {

    private Long Id;

    /**
     * sender id
     */
    private Long senderId;

    private String senderName;

    private Long senderOrganizationId;

    private String senderOrganizationName;

    /**
     * Message content (non-text display url)
     */
    private String content;

    /**
     * Creation date
     */
    private Date createTime;

    private static final long serialVersionUID = 1925318970029265222L;
}
