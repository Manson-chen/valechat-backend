package com.vale.valechat.model.dto.workspace;

import lombok.Data;

import java.io.Serializable;

/**
 * Group create request body
 *
 */
@Data
public class WorkSpaceVOList implements Serializable {
    private static final long serialVersionUID = 3394470067660778211L;

    public long workspaceId;
}