package com.vale.valechat.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WorkspaceFileListVO implements Serializable {

    List<ChatFileListVO> privateFileList;

    List<ChatFileListVO> channelFileList;

    private static final long serialVersionUID = 2442929552077122043L;
}
