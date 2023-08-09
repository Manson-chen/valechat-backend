package com.vale.valechat.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vale.valechat.common.BaseResponse;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.common.ResultUtils;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.model.dto.file.ChatFileRequest;
import com.vale.valechat.model.dto.file.WorkspaceFileRequest;
import com.vale.valechat.model.entity.ChannelFile;
import com.vale.valechat.model.entity.PrivateFile;
import com.vale.valechat.model.vo.ChatFileListVO;
import com.vale.valechat.model.vo.WorkspaceFileListVO;
import com.vale.valechat.service.ChannelFileService;
import com.vale.valechat.service.PrivateFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    @Resource
    private PrivateFileService privateFileService;

    @Resource
    private ChannelFileService channelFileService;

//    public ResponseEntity<StreamingResponseBody> getResource(String filePath, String fileType, String fileName){
//
//    }

    @GetMapping("/{workspaceId}/{fileId}/{privateFileName}")
    public ResponseEntity<StreamingResponseBody> getPrivateFile(@PathVariable long workspaceId, @PathVariable String fileId, @PathVariable String privateFileName){
        // 根据文件ID查找文件存储路径
        QueryWrapper<PrivateFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("unique_name", fileId);
        PrivateFile privateFile = privateFileService.getOne(queryWrapper);

        if (privateFile == null){
            throw new BusinessException(ErrorCode.NULL_ERROR, "This private file doesn't exist");
        }

//        PrivateFile privateFile = privateFileService.getById(fileId);
        String filePath = privateFile.getFilePath();
        String fileType = privateFile.getFileType();
        String fileName = privateFile.getFileName();

        // 组合完整的文件路径
//        String filePath = fileStoragePath + "/" + fileName;
        // 读取文件
        try {
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            StreamingResponseBody responseBody = outputStream -> {
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
            };
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(fileType));
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(fileName)
                    .build());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(responseBody);

        } catch (FileNotFoundException e) {
            throw new BusinessException(ErrorCode.NULL_ERROR, e.getMessage());
        }

//        return getResource(filePath, fileType, fileName);
    }

    @GetMapping("/{workspaceId}/{channelId}/{fileId}/{channelFileName}")
    public ResponseEntity<StreamingResponseBody> getChannelFile(@PathVariable long workspaceId, @PathVariable long channelId, @PathVariable String fileId, @PathVariable String channelFileName){
        // 根据文件ID查找文件存储路径
        QueryWrapper<ChannelFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("unique_name", fileId);
        ChannelFile channelFile = channelFileService.getOne(queryWrapper);

        if (channelFile == null){
            throw new BusinessException(ErrorCode.NULL_ERROR, "This channel file doesn't exist");
        }

        String filePath = channelFile.getFilePath();
        String fileType = channelFile.getFileType();
        String fileName = channelFile.getFileName();

        // 组合完整的文件路径
//        String filePath = fileStoragePath + "/" + fileName;

        // 读取文件
        try {
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            StreamingResponseBody responseBody = outputStream -> {
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
            };
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(fileType));
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(fileName)
                    .build());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(responseBody);

        } catch (FileNotFoundException e) {
            throw new BusinessException(ErrorCode.NULL_ERROR, e.getMessage());
        }
    }

    @PostMapping("/chat/search")
    public BaseResponse<IPage<ChatFileListVO>> searchChatFileByName(@Valid @RequestBody ChatFileRequest chatFileRequest){
//        if (StringUtils.isAnyBlank())
        int chatType = chatFileRequest.getChatType();
        IPage<ChatFileListVO> chatFileListVOList = null;
        if (chatType == 0){
            chatFileListVOList = privateFileService.getChatFileByName(chatFileRequest);
        } else if (chatType == 1){
            chatFileListVOList =  channelFileService.getChatFileByName(chatFileRequest);
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "chatType is wrong");
        }
//        if (chatFileVOList == null || chatFileVOList.size() == 0){
//            throw new BusinessException(ErrorCode.NULL_ERROR);
//        }
        return ResultUtils.success(chatFileListVOList);
    }

    @PostMapping("/workspace/search")
    public BaseResponse<WorkspaceFileListVO> searchWorkspaceFileByName(@Valid @RequestBody WorkspaceFileRequest workspaceFileRequest){
        long workspaceId = workspaceFileRequest.getWorkspaceId();
        String fileName = workspaceFileRequest.getFileName();
        if(StringUtils.isAnyBlank(String.valueOf(workspaceId))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<ChatFileListVO> privateFileList = privateFileService.getWorkspaceFileByName(workspaceFileRequest);
        List<ChatFileListVO> channelFileList = channelFileService.getWorkspaceFileByName(workspaceFileRequest);
        WorkspaceFileListVO workspaceFileListVO = new WorkspaceFileListVO();
        workspaceFileListVO.setPrivateFileList(privateFileList);
        workspaceFileListVO.setChannelFileList(channelFileList);
        return ResultUtils.success(workspaceFileListVO);
    }

}
