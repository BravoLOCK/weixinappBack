package com.xiaowei.demo.controller;

import com.xiaowei.demo.common.Result;
import com.xiaowei.demo.utils.UploadFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private UploadFileUtil uploadFileUtil;

    @PostMapping("/file")
    public Result<?> uploadFile(@RequestParam("avatar") MultipartFile file, @RequestParam String type) throws IOException {
        log.info("上传文件：{}", file);
        String fileUrl = uploadFileUtil.upload(file, type);
        if (fileUrl == null || fileUrl == "") {
            return Result.error("上传失败");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("fileUrl", fileUrl);

        return Result.success(data);
    }

}

