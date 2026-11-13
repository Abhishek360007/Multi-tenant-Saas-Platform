package com.abhishek.tenanthub.system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


/**
 * [ Common ] File records table
 *
 * @TableName sys_file_detail
 */

@TableName(value = "sys_file_detail")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysFileDetail implements java.io.Serializable {

    /**
     * File id
     */
    @TableId
    private String id;

    /**
     * File URL
     */
    private String url;

    /**
     * File size in bytes
     */
    private Long size;

    /**
     * File name
     */
    private String filename;

    /**
     * Original file name
     */
    private String originalFilename;

    /**
     * Base storage path
     */
    private String basePath;

    /**
     * Storage path
     */
    private String path;

    /**
     * File extension
     */
    private String ext;

    /**
     * MIME Type
     */
    private String contentType;

    /**
     * Storage platform
     */
    private String platform;

    /**
     * Thumbnail URL
     */
    private String thUrl;

    /**
     * Thumbnail name
     */
    private String thFilename;

    /**
     * Thumbnail size in bytes
     */
    private Long thSize;

    /**
     * Thumbnail MIME type
     */
    private String thContentType;

    /**
     * Owner object id
     */
    private String objectId;

    /**
     * Owner object type, e.g. user avatar or review image
     */
    private String objectType;

    /**
     * Extra attributes
     */
    private String attr;

    /**
     * Created at
     */
    private LocalDateTime createTime;

}