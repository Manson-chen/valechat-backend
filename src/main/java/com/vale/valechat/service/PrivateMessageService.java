package com.vale.valechat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vale.valechat.model.dto.message.CommonMessageRequest;
import com.vale.valechat.model.dto.message.PrivateHistoryRequest;
import com.vale.valechat.model.entity.PrivateMessage;
import com.vale.valechat.model.vo.ChannelMessageVo;
import com.vale.valechat.model.vo.CommonMessageVO;
import com.vale.valechat.model.vo.SearchMessageVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author jiandongchen
* @description 针对表【private_message】的数据库操作Service
* @createDate 2023-04-05 19:38:30
*/
public interface PrivateMessageService extends IService<PrivateMessage> {

    Page<CommonMessageVO> getHistoryByPrivate(PrivateHistoryRequest privateHistoryRequest, HttpServletRequest request);

    long saveMessage(CommonMessageRequest commonMessageRequest);

    List<SearchMessageVO> searchChat(long senderId, long receiverid, long workspaceId, String searchWord, long current, long pageSize);

    CommonMessageVO chatWithAi(CommonMessageRequest commonMessageRequest);
}
