package com.vale.valechat.model.vo;

import lombok.Data;

import java.util.Date;
import java.io.Serializable;

@Data
public class OrganizationVo implements Serializable {
    private static final long serialVersionUID = 394676281050808340L;
    private Long id;
    private String organizationName;
    private String Email;
    private String organizationAvatar;

}
