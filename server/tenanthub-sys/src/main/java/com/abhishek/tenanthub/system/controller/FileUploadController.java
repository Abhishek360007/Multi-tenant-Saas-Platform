package com.abhishek.tenanthub.system.controller;

import cn.hutool.core.io.FileUtil;
import com.abhishek.tenanthub.common.exception.BizException;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.system.po.ImageSizePO;
import com.abhishek.tenanthub.system.vo.FileInfoVO;
import com.abhishek.tenanthub.web.repeat.NoRepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.platform.LocalPlusFileStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/1/6 16:28
 */


@Slf4j
@Tag(name = "[ Common ] File upload")
@RestController
@AllArgsConstructor
@RequestMapping("/upload")
public class FileUploadController {

    private final FileStorageService storageService;

    /**
     * Serves files saved by local disk storage under /files/** (see storage settings in application.yml).
     * With S3-compatible storage the files are served by the bucket or CDN instead.
     *
     * @link <a href="https://x-file-storage.xuyanwu.cn/#/">Documentation</a>
     */
    @Bean
    public WebMvcConfigurer localFileStorageWebMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
                storageService.getFileStorageList().stream()
                        .filter(LocalPlusFileStorage.class::isInstance)
                        .map(LocalPlusFileStorage.class::cast)
                        .findFirst()
                        .ifPresent(local -> registry.addResourceHandler("/files/**")
                                // toUri() gives a valid file: URL on every OS (e.g. file:///D:/app/uploads/)
                                .addResourceLocations(java.nio.file.Path.of(local.getStoragePath()).toAbsolutePath().toUri().toString()));
            }
        };
    }

    /**
     * Upload a file; returns the file url
     */
    @NoRepeatSubmit
    @Operation(summary = "Image upload", description = "Image upload")
    @PostMapping("/image")
    public JsonResult<FileInfoVO> image(MultipartFile file, ImageSizePO size) {
        try {
            isImage(file.getOriginalFilename());
            var contentType = getContentType(file.getOriginalFilename());
            var now = LocalDate.now();
            var up = storageService.of(file)
                    .setObjectType("image")
                    .setContentType(contentType)
                    .setPath(
                            String.format("%d/%d/%d/", now.getYear(), now.getMonthValue(), now.getDayOfMonth())
                    );
            if (size.getHeight() != null && size.getWidth() != null) {
                up.image(size.getWidth(), size.getHeight());
            }
            var fileInfo = up.upload();
            return JsonResult.success(
                    new FileInfoVO()
                            .setUrl(fileInfo.getUrl())
                            .setFilename(fileInfo.getFilename())
                            .setExt(fileInfo.getExt())
            );
        } catch (RuntimeException e) {
            return JsonResult.fail(e.getMessage(), FileInfoVO.class);
        }
    }


    /**
     * Avatar upload; returns the compressed file URL
     */
    @NoRepeatSubmit
    @Operation(summary = "Avatar upload", description = "Avatar upload, resized to 200 * 200")
    @PostMapping("/avatar")
    public JsonResult<FileInfoVO> avatar(MultipartFile file) {
        try {
            isImage(file.getOriginalFilename());
            var contentType = getContentType(file.getOriginalFilename());
            var now = LocalDate.now();
            var fileInfo = storageService.of(file)
                    .setObjectType("avatar")
                    .setContentType(contentType)
                    .image()  // Resize the image to 200 * 200
                    .setPath(
                            String.format("%d/%d/%d/", now.getYear(), now.getMonthValue(), now.getDayOfMonth())
                    )
                    .upload();
            return JsonResult.success(
                    new FileInfoVO()
                            .setUrl(fileInfo.getUrl())
                            .setFilename(fileInfo.getFilename())
                            .setExt(fileInfo.getExt())
            );
        } catch (RuntimeException e) {
            return JsonResult.fail(e.getMessage(), FileInfoVO.class);
        }
    }


    /**
     * Upload firmware
     *
     * @param fileUrl
     * @author Wang Chen Chen
     * @date 2021/7/19 18:28
     */
    @NoRepeatSubmit
    @Operation(summary = "Delete file", description = "Delete a file")
    @DeleteMapping
    public JsonResult<Boolean> delete(String fileUrl) {
        var ok = storageService.delete(fileUrl);
        return JsonResult.success(ok);
    }



    private static final String IMAGE_REG = ".+(.JPEG|.jpeg|.JPG|.jpg|.png|.PNG|.bmp|.BMP|.webp|.WEBP)$";

    /**
     * Check whether it is an image
     *
     * @param imageName Image name
     * @return boolean true,passed, false: failed
     */
    public static void isImage(String imageName) {
        if (StringUtils.isBlank(imageName)) {
            throw new BizException("Image name is required");
        }
        if (!imageName.matches(IMAGE_REG)) {
            throw new BizException("Only jpg, jpeg, png, bmp or webp can be uploaded");
        }
    }


    /**
     * Decide by file extension ContentType
     *
     * @author WangChenChen
     * @date 2023/1/6 16:50
     */
    public static String getContentType(String fileName) {
        return switch (FileUtil.extName(fileName)) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "png" -> "image/png";
            case "ico" -> "image/x-icon";
            case "bmp" -> "image/bmp";
            case "wbmp" -> "image/vnd.wap.wbmp";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "xml" -> "application/xml";
            case "json" -> "application/json";
            case "pdf" -> "application/pdf";
            case "doc", "docx" -> "application/msword";
            case "ppt", "pptx" -> "application/vnd.ms-powerpoint";
            case "xls", "xlsx" -> "application/vnd.ms-excel";
            case "html" -> "text/html";
            case "txt" -> "text/plain";
            case "yaml" -> "text/vnd.yaml";
            case "yml" -> "text/vnd.yml";
            case "mp3" -> "audio/mp3";
            case "mp4" -> "video/mp4";
            case "avi" -> "video/avi";
            case "mpg" -> "video/mpg";
            default -> "application/octet-stream";
        };
    }

}
