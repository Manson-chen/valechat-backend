package com.vale.valechat.manager;

import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * AI 对话
 */
@Service
public class AiManager {

    @Resource
    private YuCongMingClient client;

    public String doChat(String message) {
        // 构造请求
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(1685838815933083649L);
        devChatRequest.setMessage(message);
        // 获取响应
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        if (response == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应出错");
        }
        return response.getData().getContent();
    }

}
