package com.abhishek.tenanthub.system.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/13 16:07
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Image size")
public class ImageSizePO implements java.io.Serializable {

    @Schema(description = "Width")
    private Integer width;

    @Schema(description = "Height")
    private Integer height;

}
