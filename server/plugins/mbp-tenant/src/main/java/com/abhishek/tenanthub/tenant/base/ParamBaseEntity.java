package com.abhishek.tenanthub.tenant.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Project base class
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 13:39
 */

@Getter
@Setter
public class ParamBaseEntity extends BaseEntity implements java.io.Serializable {

    /**
     * Parameters
     */
    @JsonIgnore
    @TableField(exist = false)
    private Map<String, Object> params;

    @JsonIgnore
    public Map<String, Object> getParamsValue() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParamsValue(Map<String, Object> params) {
        this.params = params;
    }

}
