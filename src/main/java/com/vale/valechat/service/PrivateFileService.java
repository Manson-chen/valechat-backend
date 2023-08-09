package com.vale.valechat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vale.valechat.model.dto.file.ChatFileRequest;
import com.vale.valechat.model.dto.file.WorkspaceFileRequest;
import com.vale.valechat.model.dto.message.CommonMessageRequest;
import com.vale.valechat.model.entity.PrivateFile;
import com.vale.valechat.model.vo.ChatFileListVO;
import com.vale.valechat.model.vo.FileListVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author jiandongchen
* @description 针对表【private_file】的数据库操作Service
* @createDate 2023-04-05 19:39:27
*/
public interface PrivateFileService extends IService<PrivateFile> {

    List<FileListVO> saveMessageFiles(CommonMessageRequest commonMessageRequest, long messageId, MultipartFile[] files, HttpServletRequest request);

    IPage<ChatFileListVO> getChatFileByName(ChatFileRequest chatFileRequest);

    List<ChatFileListVO> getWorkspaceFileByName(WorkspaceFileRequest workspaceFileRequest);
}
