package com.abhishek.tenanthub.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/1/6 18:11
 */

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoVO implements java.io.Serializable {

    /**
     * File URL
     */
    @Schema(description = "File URL")
    private String url;

    /**
     * File name
     */
    @Schema(description = "File name")
    private String filename;

    /**
     * File extension
     */
    @Schema(description = "File extension")
    private String ext;

}
