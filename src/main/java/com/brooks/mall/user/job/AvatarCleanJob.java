package com.brooks.mall.user.job;

import com.brooks.mall.user.config.FileUploadConfig;
import com.brooks.mall.user.entity.User;
import com.brooks.mall.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvatarCleanJob {

    private final FileUploadConfig fileUploadConfig;

    private final UserService userService;
    // 这里填入你配置文件中头像存储的绝对路径，或者注入你的 FileUploadConfig
    private final String uploadPath = "D:/ActionSoft/your/upload/path/"; 

    /**
     * 每天凌晨 3 点执行一次清理
     * 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanExpiredAvatars() {
        log.info("====== 开始执行过期头像清理任务 ======");
        
        try {
            // 1. 从数据库查出所有正在使用的头像文件名
            List<User> allUsers = userService.getUsers();
            Set<String> activeAvatars = allUsers.stream()
                    .map(User::getAvatar)
                    .filter(avatar -> avatar != null && !avatar.isEmpty())
                    // 提取文件名，例如从 /api/images/abc.jpg 中提取 abc.jpg
                    .map(avatar -> avatar.substring(avatar.lastIndexOf("/") + 1))
                    .collect(Collectors.toSet());

            // 2. 扫描本地磁盘的头像文件夹
            String uploadPath = fileUploadConfig.getUploadPath();
            File dir = new File(uploadPath);
            if (!dir.exists() || !dir.isDirectory()) {
                log.warn("头像目录不存在: {}", uploadPath);
                return;
            }

            File[] files = dir.listFiles((d, name) -> 
                name.endsWith(".jpg") || name.endsWith(".png"));
            
            if (files == null) return;

            // 3. 对比并删除孤儿文件
            int deletedCount = 0;
            for (File file : files) {
                if (!activeAvatars.contains(file.getName())) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        deletedCount++;
                        log.debug("成功删除孤儿头像: {}", file.getName());
                    }
                }
            }
            log.info("====== 头像清理任务完成，共清理 {} 个废弃文件 ======", deletedCount);
            
        } catch (Exception e) {
            log.error("头像清理任务执行异常", e);
        }
    }
}