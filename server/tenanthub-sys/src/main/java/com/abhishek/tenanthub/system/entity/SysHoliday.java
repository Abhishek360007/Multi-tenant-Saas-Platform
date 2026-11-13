package com.abhishek.tenanthub.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * Indian public holiday (shared by all tenants).
 *
 * @author Abhishek Raj
 */
@Schema(description = "Indian public holiday")
@TableName("sys_holiday")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class SysHoliday implements java.io.Serializable {

    @TableId(type = IdType.INPUT)
    @Schema(description = "Holiday date")
    @NotNull(message = "Holiday date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate holidayDate;

    @Schema(description = "Holiday name, e.g. Republic Day")
    @NotBlank(message = "Holiday name is required")
    private String name;

    @Schema(description = "gazetted (closed for all central government offices) or restricted (optional)")
    @Pattern(regexp = "^(gazetted|restricted)$", message = "Category must be gazetted or restricted")
    private String category;

    @Schema(description = "Where the date comes from, e.g. the DoPT office memorandum")
    private String source;
}
