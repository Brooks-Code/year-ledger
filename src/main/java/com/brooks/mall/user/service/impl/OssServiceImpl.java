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
            throw new BusinessException("文件不能为空");
        }
        User user = UserContext.getUser();
        String oldAvatarUrl = user.getAvatar();

        // 生成唯一的文件名（防止重名覆盖）
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String objectKey = "avatars/" + UUID.randomUUID() + suffix; // 存放在 avatars 目录下

        // 2. 流式上传到 OSS
        try {
            ossClient.putObject(bucketName, objectKey, file.getInputStream());
            //删除旧头像
            if (oldAvatarUrl != null) {
                String oldObjectKey = extractObjectKeyFromUrl(oldAvatarUrl);
                if (oldObjectKey != null) {
                    deleteFile(oldObjectKey);
                }
            }
        } catch (IOException e) {
            throw new BusinessException("文件上传失败:" + e);
        }
        String url = "https://" + bucketName + "." + endpoint + "/" + objectKey;
        // 图片上传成功后，将图片访问路径保存到数据库中
        userMapper.updateAvatar(user.getUserId(), url);

        // 3. 拼接并返回头像的公网访问链接
        return url;
    }

    /**
     * 删除文件
     */
    @Override
    public void deleteFile(String objectKey) {
        try {
            ossClient.deleteObject(bucketName, objectKey);
            System.out.println("✅ 旧头像删除成功: " + objectKey);
        } catch (Exception e) {
            throw new BusinessException("文件删除失败:" + e);
        }
    }

    /**
     * 从完整的 OSS URL 中提取 ObjectKey
     *
     * @param fullUrl 完整的 OSS 访问链接
     * @return 提取出的 ObjectKey，如果解析失败则返回 null
     */
    public String extractObjectKeyFromUrl(String fullUrl) {
        if (fullUrl == null || fullUrl.isEmpty()) {
            return null;
        }

        // 构造出当前 Bucket 的域名前缀，例如：https://my-bucket.oss-cn-hangzhou.aliyuncs.com/
        String bucketPrefix = "https://" + bucketName + "." + endpoint + "/";

        // 如果 URL 包含该前缀，则截取后面的部分作为 ObjectKey
        if (fullUrl.startsWith(bucketPrefix)) {
            return fullUrl.substring(bucketPrefix.length());
        }

        // 如果 URL 格式不匹配（比如传了默认头像的本地路径），返回 null
        return null;
    }
}