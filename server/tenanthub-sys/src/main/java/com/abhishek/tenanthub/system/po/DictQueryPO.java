package com.abhishek.tenanthub.system.po;

import com.abhishek.tenanthub.common.po.SearchPO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


/**
 * <p>
 * Dictionary query parameters
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 2.0
 * @date 2019/4/18 11:45
 */

@Data
@ToString
@Schema(description = "Dictionary query")
@EqualsAndHashCode(callSuper = false)
public class DictQueryPO extends SearchPO {

    /**
     * Dictionary type
     */
    @Schema(description = "Dictionary type", requiredMode = REQUIRED)
    private String dictTypeKey;

}
