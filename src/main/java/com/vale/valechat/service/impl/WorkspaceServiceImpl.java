package com.vale.valechat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.vale.valechat.mapper.WorkspaceMapper;
import com.vale.valechat.model.entity.Workspace;
import com.vale.valechat.service.WorkspaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WorkspaceServiceImpl extends ServiceImpl<WorkspaceMapper, Workspace>
    implements WorkspaceService{

}




