package com.vale.valechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vale.valechat.model.entity.ChatGroup;

import java.util.List;

public interface ChatGroupService extends IService<ChatGroup> {
    ChatGroup CreateNewGroup(Long masterId, String GroupName);

    //int MessageSave();
}
