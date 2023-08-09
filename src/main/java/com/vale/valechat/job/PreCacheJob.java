package com.vale.valechat.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vale.valechat.model.entity.User;
import com.vale.valechat.model.entity.UserWorkspace;
import com.vale.valechat.model.entity.Workspace;
import com.vale.valechat.model.vo.ChatListVO;
import com.vale.valechat.service.UserService;
import com.vale.valechat.service.UserWorkspaceService;
import com.vale.valechat.service.WorkspaceService;
import com.vale.valechat.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private UserWorkspaceService userWorkspaceService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    // 每天执行，预热推荐用户
    // todo 开启定时任务：缓存预热
    @Scheduled(fixedRate = 3600000)
    public void doCacheChatList() {
        RLock lock = redissonClient.getLock("valechat:precachejob:chatlist:lock");
//        RLock lock = redissonClient.getLock("valechat:precachejob:chatlist:lock");
        try {
            // 只有一个线程能获取到锁
            // 第一个参数：只要第一次没获取到就不等待第二次获取了，所以是0
            // 第二个参数：设置为-1，则自动续期（获得锁的线程多长时间释放锁）
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());
                // 原本是根据不同workspace的用户去一个个缓存，之后再改进
//                List<UserWorkspace> userWorkspaceList = userWorkspaceService.list();
//                for (UserWorkspace userWorkspace : userWorkspaceList) {
                List<User> userList = userService.list();
                for (User user : userList) {
                    Long userId = user.getId();
                    String redisKey = String.format("valechat:worksapce:%s:user:chatlist:%s", 1, userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    ChatListVO chatListVO = userService.getUserChatList(1, userId);
                    // 写缓存
                    try {
                        valueOperations.set(redisKey, chatListVO, 3600000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheChatList error", e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    // 每天执行，预热推荐用户
    @Scheduled(cron = "0 31 0 * * *")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            // 第一个参数：只要第一次没获取到就不等待第二次获取了，所以是0
            // 第二个参数：设置为-1，则自动续期（获得锁的线程多长时间释放锁）
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("yupao:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    // 写缓存
                    try {
                        valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}
