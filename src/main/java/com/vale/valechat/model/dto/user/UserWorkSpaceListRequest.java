package com.vale.valechat.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserWorkSpaceListRequest implements Serializable{

    private static final long serialVersionUID = 3172303580976993010L;

    private Long userId;
}