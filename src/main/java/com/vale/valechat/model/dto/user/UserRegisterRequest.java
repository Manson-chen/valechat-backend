package com.vale.valechat.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3394470067660778211L;

    private String userAccount;

    private String email;

    private String userName;

    /**
     * user role (0-common User, 1-institute)
     */
    @Schema(description = "0-common user, 1-organization user")
    private Integer userRole;

    /**
     * Referenced organization_id in organization
     */
    private Long organizationId;

    private String userPassword;

    private String checkPassword;

}
