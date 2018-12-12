package com.CHY.shoppingGo.manager.controller;

import com.CHY.shoppingGo.entity.Result;
import com.CHY.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("uploadFile")
    public Result uploadFile(MultipartFile file){
        try {
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            String returnPath = client.uploadFile(file.getBytes(), file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1), null);
            return new Result(true,FILE_SERVER_URL+returnPath);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
