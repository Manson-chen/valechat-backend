package com.vale.valechat.model.vo;

import com.vale.valechat.model.entity.Channel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WorkSpaceVo implements Serializable {
    private static final long serialVersionUID = 7873172398643421717L;
    private Long id;

    /**
     * workspace name
     */
    private String workspaceName;

    /**
     * the id of the user who created this workspace.
     */
    private Long masterId;
}
