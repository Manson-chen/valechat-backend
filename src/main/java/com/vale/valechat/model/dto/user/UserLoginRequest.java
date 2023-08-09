package com.vale.valechat.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * User login request body
 *
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3394470067660778211L;

    private String userAccount;

    private String userPassword;


}
