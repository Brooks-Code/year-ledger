package com.brooks.mall.user.service.impl;

import com.aliyun.oss.OSS;
import com.brooks.mall.user.entity.User;
import com.brooks.mall.user.exception.BusinessException;
import com.brooks.mall.user.mapper.UserMapper;
import com.brooks.mall.user.service.OssService;
import com.brooks.mall.user.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    @Autowired
    private OSS ossClient;
    @Autowired
    private UserMapper userMapper;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    /**
     * 上传头像并返回访问链接
     */
    public String uploadAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 1. 生成唯一的文件名（防止重名覆盖）
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String objectKey = "avatars/" + UUID.randomUUID() + suffix; // 存放在 avatars 目录下

        // 2. 流式上传到 OSS
        try {
            ossClient.putObject(bucketName, objectKey, file.getInputStream());
        } catch (IOException e) {
            throw new BusinessException("文件上传失败:" + e);
        }
        String url = "https://" + bucketName + "." + endpoint + "/" + objectKey;
        // 图片上传成功后，将图片访问路径保存到数据库中
        User user = UserContext.getUser();
        userMapper.updateAvatar(user.getUserId(), url);

        // 3. 拼接并返回头像的公网访问链接
        return url;
    }
}