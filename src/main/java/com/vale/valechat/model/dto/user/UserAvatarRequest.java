package com.vale.valechat.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * User update profile request body
 *
 */
@Data
public class UserAvatarRequest implements Serializable {

    private static final long serialVersionUID = 3394470067660778211L;

    @NotNull(message = "user id can't be null")
    private Long id;

    @Schema(description = "avatar file")
    @NotNull(message = "Upload file cannot be empty")
    @Size(max = 50 * 1024 * 1024, message = "Upload file size cannot exceed 50MB")
    private MultipartFile file;

}
