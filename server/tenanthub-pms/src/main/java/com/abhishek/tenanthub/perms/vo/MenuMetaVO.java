package com.abhishek.tenanthub.perms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/1/6 14:16
 */

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MenuMetaVO implements java.io.Serializable {

    /**
     * Menu icon
     */
    @Schema(description = "Menu icon")
    private String icon;

    /**
     * Menu title
     */
    @Schema(description = "Menu title")
    private String title;

    /**
     * Hidden
     */
    @Schema(description = "Hidden")
    private Boolean hidden;

    /**
     * Hidden
     */
    @Schema(description = "Keep alive (1 = yes, 0 = no)")
    private Boolean keepAlive;

}
