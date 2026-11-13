package com.abhishek.tenanthub.common.consts;

/**
 * <p>
 * Global push messages
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2023/11/08 15:15
 */


public class SimpMessageConst {

    // Broadcast notification
    public static final String BROADCAST_NOTICE = "/topic/broadcast/notice";

    // Targeted push
    public static final String QUEUE_SINGLE_PUSH = "/queue/single/push";

    // Push on tenant creation success or failure
    public static final String QUEUE_SINGLE_CREATE_TENANT = "/queue/single/create/tenant";

}
