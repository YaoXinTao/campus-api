package com.campus.api.controller.mini;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.campus.api.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "小程序文件接口", description = "小程序文件上传相关接口")
@RestController
@RequestMapping("/api/v1/mini")
public class MiniFileController {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Operation(summary = "文件上传", description = "上传文件到阿里云OSS")
    @PostMapping("/file/upload")
    public Result<String> uploadFile(
            @Parameter(description = "文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "目录", required = false) @RequestParam(value = "dir", defaultValue = "mini") String dir) {
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;
        
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        
        try {
            // 上传文件
            ossClient.putObject(bucketName, dir + "/" + newFileName, file.getInputStream());
            
            // 获取文件访问路径
            String url = "https://" + bucketName + "." + endpoint + "/" + dir + "/" + newFileName;
            return Result.success(url);
        } catch (IOException e) {
            return Result.error(500, "文件上传失败：" + e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }
} 