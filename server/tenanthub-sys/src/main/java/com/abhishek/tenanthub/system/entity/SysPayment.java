package com.abhishek.tenanthub.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * A subscription payment through Razorpay. Amounts in paise.
 *
 * @author Abhishek Raj
 */
@Schema(description = "Subscription payment (amounts in paise)")
@TableName("sys_payment")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class SysPayment implements java.io.Serializable {

    public static final String CREATED = "created";
    public static final String PAID = "paid";
    public static final String FAILED = "failed";

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String tenantId;

    private String planCode;

    @Schema(description = "MONTHLY or YEARLY")
    private String billingCycle;

    private Long basePaise;

    private Long cgstPaise;

    private Long sgstPaise;

    private Long igstPaise;

    private Long totalPaise;

    @Schema(description = "Tenant GSTIN at the time of purchase")
    private String buyerGstin;

    @Schema(description = "GST state code of the buyer")
    private Integer placeOfSupply;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    @Schema(description = "created, paid or failed")
    private String status;

    @Schema(description = "Invoice number, e.g. TH/2026-27/000001")
    private String invoiceNo;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;
}
