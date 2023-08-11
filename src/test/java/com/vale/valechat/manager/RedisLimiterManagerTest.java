package com.vale.valechat.manager;

import com.yupi.yuapiclientsdk.client.ApiClient;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    RedisLimiterManager redisLimiterManager;

//    @Resource
//    private ApiClient client;


    @Test
    void doRateLimit() throws InterruptedException {
        String userId = "1";
        for (int i = 0; i < 2; i++) {
            redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }
        Thread.sleep(1000);
        for (int i = 0; i < 5; i++) {
            redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }

//        String goodText = client.getGoodText();
//        String image = client.getImage();
//        String ipStatus = client.getIpStatus("ip address");
//        String qqStatus = client.getQqStatus("qq number");
//        String history = client.getTodayInHistory();


    }


}