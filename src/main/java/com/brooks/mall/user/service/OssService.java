package com.brooks.mall.user.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/8/17 17:50
 */
public interface OssService {
    /**
     * 处理头像上传业务
     *
     * @param file 前端传来的文件
     * @return 图片的访问 URL
     */
    String uploadAvatar(MultipartFile file);
}
