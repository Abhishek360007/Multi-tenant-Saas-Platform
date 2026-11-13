package com.abhishek.tenanthub.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Subscription plan. Prices are in paise (₹1 = 100 paise) and exclude GST.
 *
 * @author Abhishek Raj
 */
@Schema(description = "Subscription plan (prices in paise, before GST)")
@TableName("sys_plan")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class SysPlan implements java.io.Serializable {

    @TableId(type = IdType.INPUT)
    @Schema(description = "Plan code, e.g. free / basic / pro")
    @NotBlank(message = "Plan code is required")
    private String planCode;

    @Schema(description = "Plan name")
    @NotBlank(message = "Plan name is required")
    private String name;

    @Schema(description = "Short description")
    private String description;

    @Schema(description = "Monthly price in paise, before GST")
    @NotNull(message = "Monthly price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Long monthlyPricePaise;

    @Schema(description = "Yearly price in paise, before GST")
    @NotNull(message = "Yearly price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Long yearlyPricePaise;

    @Schema(description = "Maximum users per tenant, -1 = unlimited")
    @NotNull(message = "Maximum users is required")
    @Min(value = -1, message = "Use -1 for unlimited")
    private Integer maxUsers;

    @Schema(description = "Maximum projects per tenant, -1 = unlimited")
    @NotNull(message = "Maximum projects is required")
    @Min(value = -1, message = "Use -1 for unlimited")
    private Integer maxProjects;

    @Schema(description = "Sort order")
    private Integer sort;

    @Schema(description = "1 = offered, 0 = hidden")
    private Byte status;
}
