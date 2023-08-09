package com.vale.valechat.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * User update profile request body
 *
 */
@Data
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID = 3394470067660778211L;

    private Long id;

    private String userName;

    /**
     * gender (0-male, 1-female)
     */
    @Schema(description = "0-male, 1-female")
    private Integer gender;

    /**
     * phone number
     */
    private String phone;

    /**
     * user login status (0-offline, 1-online)
     */
    @Schema(description = "0-offline, 1-online")
    private Integer userStatus;

}
