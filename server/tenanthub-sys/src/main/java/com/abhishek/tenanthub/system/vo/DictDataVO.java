package com.abhishek.tenanthub.system.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * Dictionary data table
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/15 10:42
 */

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class DictDataVO implements java.io.Serializable {

    /**
     * Dictionary sort order
     */
    @Schema(description = "Dictionary sort order")
    private Long dictSort;

    /**
     * Dictionary label
     */
    @Schema(description = "Dictionary label")
    private String dictLabel;

    /**
     * Dictionary value
     */
    @Schema(description = "Dictionary value")
    private Object dictValue;

    /**
     * Dictionary type key
     */
    @JsonIgnore
    private String typeKey;

    /**
     * Is default (1 = yes, 0 = no)
     */
    @Schema(description = "Default")
    private Boolean isDefault;

}
