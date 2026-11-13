package com.abhishek.tenanthub.perms.vo;

import cn.hutool.core.lang.tree.Tree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/1/6 13:57
 */


@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "User permissions")
public class UserRightsVO implements java.io.Serializable {

    @Schema(description = "Button permissions")
    private Set<ButtonVO> buttons;

    @Schema(description = "Menu permissions")
    private List<Tree<Long>> menus;

}
