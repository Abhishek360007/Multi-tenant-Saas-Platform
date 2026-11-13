package com.abhishek.tenanthub.common.consts;

/**
 * <p>
 *
 * </p>
 *
 * @author WangChenChen
 * @version 1.0.0
 * @date 2024/10/28 PM 6:37
 */


public class DataScopeConst {

    /**
     * All data
     */
    public static final int DATA_SCOPE_ALL = 1;

    /**
     * Custom data
     */
    public static final int DATA_SCOPE_CUSTOM = 2;

    /**
     * Department data
     */
    public static final int DATA_SCOPE_DEPT = 3;

    /**
     * Department and below
     */
    public static final int DATA_SCOPE_DEPT_AND_CHILD = 4;

    /**
     * Own data only
     */
    public static final int DATA_SCOPE_SELF = 5;

    /**
     * Data permission filter key
     */
    public static final String DATA_SCOPE = "dataScope";


}
