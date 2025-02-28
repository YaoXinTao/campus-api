package com.campus.api.controller.admin;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.campus.api.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
public class FileController {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;
        
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        
        try {
            // 上传文件
            ossClient.putObject(bucketName, "admin/avatar/" + newFileName, file.getInputStream());
            
            // 获取文件访问路径
            String url = "https://" + bucketName + "." + endpoint + "/admin/avatar/" + newFileName;
            return Result.success(url);
        } catch (IOException e) {
            return Result.error(500, "文件上传失败：" + e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }
} 