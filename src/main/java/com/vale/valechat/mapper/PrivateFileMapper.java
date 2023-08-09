package com.vale.valechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vale.valechat.model.entity.PrivateFile;
import com.vale.valechat.model.vo.ChatFileListVO;

import java.util.List;

/**
* @author jiandongchen
* @description 针对表【private_file】的数据库操作Mapper
* @createDate 2023-04-05 19:39:27
* @Entity generator.domain.PrivateFile
*/
public interface PrivateFileMapper extends BaseMapper<PrivateFile> {

    IPage<ChatFileListVO> getChatFileByName(IPage<ChatFileListVO> page, long senderId, long receiverId, long workspaceId, String fileName);

    List<ChatFileListVO> getWorkspaceFileByName(IPage<ChatFileListVO> page, long workspaceId, String fileName);
}




