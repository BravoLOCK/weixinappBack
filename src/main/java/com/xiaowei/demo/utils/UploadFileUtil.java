package com.xiaowei.demo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class UploadFileUtil {

    @Value("${file.upload.max-size}")
    private long maxSize = 2097152; //2M

    @Value("${file.upload.allowed-types}")
    private String allowedTypes;

    @Value("${file.upload.dir}")
    private String uploadDir;

    /**
     * 获取文件保存路径
     *
             * @return File
     * @throws FileNotFoundException
     */
    private List<File> getUploadDirectory(String dir) throws FileNotFoundException {

        File targetPath = new File(ResourceUtils.getURL("classpath:").getPath());



        if (!targetPath.exists()) {

            targetPath = new File("");
        }


        String resourcesPath = System.getProperty("user.dir") + "/src/main/resources";

        File path = new File(resourcesPath);


        File upload = new File(path.getAbsolutePath(), uploadDir+"/"+dir);
        File uploadTarget = new File(targetPath.getAbsolutePath(), uploadDir+"/"+dir);


        if (!upload.exists()) {
            upload.mkdirs();
        }
        if (!uploadTarget.exists()) {
            uploadTarget.mkdirs();
        }

        List<File> files = new ArrayList<File>();
        files.add(upload);
        files.add(uploadTarget);



        return files;
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        if (file.getSize() > maxSize) {
            throw new RuntimeException("文件大小不能超过 " + (maxSize / 1024 / 1024) + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        if (extension == null || !getAllowedExtensions().contains(extension.toLowerCase())) {
            throw new RuntimeException("不支持的文件类型，仅支持: " + allowedTypes);
        }
    }

    /**
     * 获取允许的文件扩展名集合
     */
    private Set<String> getAllowedExtensions() {
        Set<String> extensions = new HashSet<>();
        String[] types = allowedTypes.split(",");
        extensions.addAll(Arrays.asList(types));
        return extensions;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null) return null;
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex == -1 ? null : filename.substring(dotIndex + 1);
    }

    /**
     * 创建目录（如果不存在）
     */
    static void createDirectoryIfNotExists(String directory) throws IOException {
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    /**
     * 主方法
     */
    public String upload(MultipartFile myFile, String dir) throws IOException {
        String filePath = "";
        if (!myFile.isEmpty()) {
            try {
                String filename = myFile.getOriginalFilename();
                filename = UUID.randomUUID() + filename.substring(filename.lastIndexOf("."));

                List<File> files = getUploadDirectory(dir);

                File curFile = new File(files.get(0), filename);
                myFile.transferTo(curFile);
                FileCopyUtils.copy(curFile, new File(files.get(1), filename));

                filePath = filename;//当前需求下只返回文件名即可
                log.debug("访问的filePath:{}", "http://localhost:8080/api/upload/" + filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return filePath;
    }
}