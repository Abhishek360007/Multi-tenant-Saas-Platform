package com.abhishek.tenanthub.system.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * Upload token for the front end to upload directly to OSS without going through the backend
 *
 * @author WangChenChen
 * @version 2.0
 * @date 2023/11/10 17:59
 */

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadTokenVO {

    @Schema(description = "Upload credential")
    private String token;

    @Schema(description = "OSS Provider")
    private String platform;

    @Schema(description = "Bucket name")
    private String bucketName;

    @Schema(description = "Custom domain")
    private String domain;

    @Schema(description = "Path prefix")
    private String basePath;

}
