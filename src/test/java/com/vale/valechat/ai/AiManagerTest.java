package com.vale.valechat.ai;

import com.vale.valechat.manager.AiManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class AiManagerTest {

    @Resource
    private AiManager aiManager;


    @Test
    void doChat() {
        String message = "邓紫棋";
        String result = aiManager.doChat(message);
        System.out.println(result);
    }
}