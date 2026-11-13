package com.abhishek.tenanthub.perms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * <p>
 * Button permissions returned after sign-in
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 2.0
 * @date 2019/4/18 11:45
 */


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class ButtonVO implements java.io.Serializable {

    /**
     * Identifier
     */
    @Schema(description = "Button permission key")
    private String perms;

    /**
     * Title
     */
    @Schema(description = "Button permission title")
    private String title;

}
