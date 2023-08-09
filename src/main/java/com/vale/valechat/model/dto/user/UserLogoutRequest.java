package com.vale.valechat.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLogoutRequest implements Serializable{

    private static final long serialVersionUID = 1L;

    private Long userId;
}