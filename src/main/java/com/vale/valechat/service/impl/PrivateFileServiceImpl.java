package com.vale.valechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.mapper.PrivateFileMapper;
import com.vale.valechat.mapper.UserMapper;
import com.vale.valechat.mapper.WorkspaceMapper;
import com.vale.valechat.model.dto.file.ChatFileRequest;
import com.vale.valechat.model.dto.file.WorkspaceFileRequest;
import com.vale.valechat.model.dto.message.CommonMessageRequest;
import com.vale.valechat.model.entity.PrivateFile;
import com.vale.valechat.model.entity.User;
import com.vale.valechat.model.entity.Workspace;
import com.vale.valechat.model.vo.ChatFileListVO;
import com.vale.valechat.model.vo.FileListVO;
import com.vale.valechat.service.PrivateFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class PrivateFileServiceImpl extends ServiceImpl<PrivateFileMapper, PrivateFile>
    implements PrivateFileService{

    @Value(("${web.root-path}"))
    private String rootPath;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");

    @Resource
    private PrivateFileMapper privateFileMapper;

    @Resource
    private WorkspaceMapper workspaceMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public List<FileListVO> saveMessageFiles(CommonMessageRequest commonMessageRequest, long messageId, MultipartFile[] files, HttpServletRequest request) {
        List<FileListVO> visibleFilesList = new ArrayList<>();
        List<PrivateFile> saveFilesList = new ArrayList<>();
        String format = sdf.format(new Date());
        long workspaceId = commonMessageRequest.getWorkspaceId();
        String privateFilePath = "/" + workspaceId + "/private/" + format;

        for (MultipartFile file : files) {
            String oldFileName = file.getOriginalFilename(); //获取文件原名
            String newFileName = UUID.randomUUID().toString();
//                    + oldFileName.substring(oldFileName.lastIndexOf("."));
            String visibleUri = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort()  + "/api/files/" + workspaceId + "/" + newFileName + "/" + oldFileName;
            String saveUri = rootPath + privateFilePath + newFileName + oldFileName.substring(oldFileName.lastIndexOf("."));

            PrivateFile privateFile = new PrivateFile();
            privateFile.setMessageId(messageId);
            privateFile.setFileType(file.getContentType());
            //todo
//            privateFile.getFileType((file.getContentType()))
            privateFile.setUniqueName(newFileName);
            privateFile.setFileName(oldFileName);
            privateFile.setFilePath(saveUri);
            privateFile.setFileUrl(visibleUri);

            log.info("Original image file name={} Image Access Address={} Picture save real address={}",newFileName,visibleUri,saveUri);
            File saveFile = new File(saveUri);

            if(!saveFile.exists()){
                saveFile.mkdirs();
            }

            try {
                file.transferTo(saveFile);
                privateFileMapper.insert(privateFile);
                saveFilesList.add(privateFile);
                privateFile.setFileUrl(visibleUri);
                FileListVO fileListVO = new FileListVO();
                BeanUtils.copyProperties(privateFile, fileListVO);
                visibleFilesList.add(fileListVO);
            }catch (IOException e){
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Failed to upload file");
            }
        }
        return visibleFilesList;
    }

    @Override
    public IPage<ChatFileListVO> getChatFileByName(ChatFileRequest chatFileRequest) {
        Long senderId = chatFileRequest.getSenderId();
        Long receiverId = chatFileRequest.getReceiverId();
        Long workspaceId = chatFileRequest.getWorkspaceId();
        String fileName = chatFileRequest.getFileName();

        long current = chatFileRequest.getCurrent();
        long size = chatFileRequest.getPageSize() / 2;

        if (senderId == null || receiverId == null || workspaceId == null || fileName == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User sender = userMapper.selectById(senderId);
        if (sender == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "sender doesn't exist");
        }

        User receiver = userMapper.selectById(receiverId);
        if (receiver == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "sender doesn't exist");
        }

        Workspace workspace = workspaceMapper.selectById(workspaceId);
        if (workspace == null){
            throw new BusinessException(ErrorCode.NULL_ERROR, "The workspace doesn't exist");
        }

        fileName = "%" + fileName + "%";
        Page<ChatFileListVO> page = new Page<>(current, size);
        IPage<ChatFileListVO> chatFileListVOList = privateFileMapper.getChatFileByName(page, senderId, receiverId, workspaceId, fileName);

//        List<ChatFileVO> chatFileVOList = privateFileList.stream().map(privateFile -> {
//            ChatFileVO chatFileVO = new ChatFileVO();
//            BeanUtils.copyProperties(privateFile, chatFileVO);
//            return chatFileVO;
//        }).collect(Collectors.toList());

        return chatFileListVOList;
    }

    @Override
    public List<ChatFileListVO> getWorkspaceFileByName(WorkspaceFileRequest workspaceFileRequest) {
        long workspaceId = workspaceFileRequest.getWorkspaceId();
        String fileName = workspaceFileRequest.getFileName();

        long current = workspaceFileRequest.getCurrent();
        long size = workspaceFileRequest.getPageSize();

        if(StringUtils.isAnyBlank(String.valueOf(workspaceId))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Workspace workspace = workspaceMapper.selectById(workspaceId);
        if (workspace == null){
            throw new BusinessException(ErrorCode.NULL_ERROR, "The workspace doesn't exist");
        }
        fileName = "%" + fileName + "%";
        Page<ChatFileListVO> page = new Page<>(current, size);
        List<ChatFileListVO> privateFileList = privateFileMapper.getWorkspaceFileByName(page, workspaceId, fileName);

        return privateFileList;
    }
}




