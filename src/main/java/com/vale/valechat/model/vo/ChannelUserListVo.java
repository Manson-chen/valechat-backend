package com.vale.valechat.model.vo;

import com.vale.valechat.model.entity.Organization;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class ChannelUserListVo implements Serializable {
    /**
     * Private chat user list
     */
    private List<UserVO> userList;

    /**
     * Channel chat chat list
     */
    private List<OrganizationVo> organizationList;

    private static final long serialVersionUID = -4062738799703155913L;
}
