package com.vale.valechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vale.valechat.model.entity.ChannelFile;
import com.vale.valechat.model.vo.ChatFileListVO;

import java.util.List;

/**
* @author jiandongchen
* @description 针对表【channel_file】的数据库操作Mapper
* @createDate 2023-04-05 19:39:36
* @Entity generator.domain.ChannelFile
*/
public interface ChannelFileMapper extends BaseMapper<ChannelFile> {
    IPage<ChatFileListVO> getChatFileByName(IPage<ChatFileListVO> page, long channelId, long workspaceId, String fileName);

    List<ChatFileListVO> getWorkspaceFileByName(IPage<ChatFileListVO> page, long workspaceId, String fileName);
}




