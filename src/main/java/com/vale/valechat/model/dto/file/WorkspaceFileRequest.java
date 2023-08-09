package com.vale.valechat.model.dto.file;

import com.vale.valechat.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class WorkspaceFileRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 438874831935306286L;
    /**
     * corresponding workspace
     */
    @NotNull
    private Long workspaceId;

    /**
     * File name
     */
    @NotNull
    private String fileName;
}
