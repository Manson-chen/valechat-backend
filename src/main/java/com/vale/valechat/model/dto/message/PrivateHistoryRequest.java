package com.vale.valechat.model.dto.message;

import com.vale.valechat.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class PrivateHistoryRequest extends PageRequest implements Serializable{

    private static final long serialVersionUID = -676608573577588845L;

    private long senderId;

    private long receiverId;

    private long workspaceId;

}