package com.abhishek.tenanthub.internal.api;

import com.abhishek.tenanthub.internal.dto.LoginLogDTO;
import com.abhishek.tenanthub.internal.dto.OperLogDTO;

/**
 * <p>
 * Runtime log storage
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/14 10:18
 */

public interface ApiLogStorageService {


    /**
     * Save login logs to ES asynchronously
     *
     * @param loginLog
     * @author Wang Chen Chen
     * @date 2021/8/11 15:30
     */
    void asyncLoginSave(LoginLogDTO loginLog);


    /**
     * Save operation logs to ES asynchronously
     *
     * @param operLog
     * @author Wang Chen Chen
     * @date 2021/8/11 15:30
     */
    void asyncOperateSave(OperLogDTO operLog);

}
