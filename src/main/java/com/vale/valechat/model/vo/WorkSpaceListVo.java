package com.vale.valechat.model.vo;

import com.vale.valechat.model.entity.Channel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class WorkSpaceListVo implements Serializable {
    private static final long serialVersionUID = -2932675599128312006L;
    private List<WorkSpaceVo> workSpaceList;
}
