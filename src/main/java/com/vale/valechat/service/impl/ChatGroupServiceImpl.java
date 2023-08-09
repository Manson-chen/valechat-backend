package com.vale.valechat.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.mapper.ChatGroupMapper;
import com.vale.valechat.model.entity.ChatGroup;
import com.vale.valechat.service.ChatGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class ChatGroupServiceImpl extends ServiceImpl<ChatGroupMapper, ChatGroup>
        implements ChatGroupService {
    @Resource
    ChatGroupMapper chatGroupMapper;

    @Override
    public ChatGroup CreateNewGroup(Long masterId, String GroupName) {
        ChatGroup chatGroup = new ChatGroup();
        chatGroup.setMasterId(masterId);
        chatGroup.setGroupName(GroupName);
        boolean saveResult = this.save(chatGroup);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Insert database error");
        }
        return chatGroup;
    }

}
