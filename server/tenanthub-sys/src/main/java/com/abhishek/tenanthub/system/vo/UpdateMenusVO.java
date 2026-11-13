package com.abhishek.tenanthub.system.vo;

import cn.hutool.core.lang.tree.Tree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * Menus linked to roles, departments, tenant templates, etc.
 * </p>
 *
 * @author Wang Chen Chen<932560435@qq.com>
 * @version 1.0.1
 * @date 2021/7/24 14:39
 */

@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Edit menu")
public class UpdateMenusVO implements java.io.Serializable {

    /**
     * Assigned menus ID
     */
    @Schema(description = "Assigned menu IDs")
    private Set<Long> have;

    /**
     * All menu IDs and names
     */
    @Schema(description = "All menu IDs and names")
    private List<Tree<Long>> all;

}


