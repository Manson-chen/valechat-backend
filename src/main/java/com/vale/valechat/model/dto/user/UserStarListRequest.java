package com.vale.valechat.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserStarListRequest implements Serializable{

    private static final long serialVersionUID = 3172303580976993010L;

    @NotNull(message = "workspaceId can't be null")
    private Long workspaceId;

    @NotNull(message = "userId can't be null")
    private Long userId;

}