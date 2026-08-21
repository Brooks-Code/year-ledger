package com.brooks.mall.user.controller;

import com.brooks.mall.common.result.Result;
import com.brooks.mall.user.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oss")
public class OssController {
    @Autowired
    private OssService ossService;

    @PostMapping("/uploadAvatar")
    public Result<Map> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        String avatarUrl = ossService.uploadAvatar(file);
        map.put("avatarUrl", avatarUrl);
        return Result.success(map);

    }
}