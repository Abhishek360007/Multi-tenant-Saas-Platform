package com.abhishek.tenanthub.common.consts;


import com.abhishek.tenanthub.common.util.TenantUtils;

/**
 * <p>
 * es Index name
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/8/10 18:25
 */

public class ESIndexNameConst {

    /**
     * Operation log index name
     */
    private static final String OPERATE_LOG_INDEX = "_lms_oper_log";

    /**
     * Get the operation log index name
     */
    public static String getOperateLogIndex() {
        return TenantUtils.getTenantId() + OPERATE_LOG_INDEX;
    }


    /**
     * Login log
     */
    private static final String LOGIN_LOG_INDEX = "_lms_login_log";


    /**
     * Get the operation log index name
     */
    public static String getLoginLogIndex() {
        return TenantUtils.getTenantId() + LOGIN_LOG_INDEX;
    }


    /**
     * Device log
     */
    private static final String DEVICE_LOG_INDEX = "_dms_device_log";


    /**
     * Get the device log index name
     */
    public static String getDeviceLogIndex() {
        return TenantUtils.getTenantId() + DEVICE_LOG_INDEX;
    }

}
