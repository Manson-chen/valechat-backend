package com.vale.valechat.model.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserStarRequest implements Serializable {

    /**
     * The user(id) who create the star
     */
    @NotNull(message = "userId can't be null")
    @Schema(description = "The user who create the star")
    private Long userId;

    @NotNull(message = "workspaceId can't be null")
    private Long workspaceId;

    /**
     * 2 Type: 0-user, 1-channel
     */
    @NotNull(message = "starType can't be null")
    @Schema(description = "3 Types: 0-user, 1-channel, 2-orgChannel")
    private Integer starType;


    /**
     * If star_type=0, starred_id=user_id, otherwise channel_id
     */
    @NotNull(message = "starredId can't be null")
    @Schema(description = "If star_type=0, starred_id=user_id, otherwise channel_id")
    private Long starredId;

    private static final long serialVersionUID = 1L;
}
