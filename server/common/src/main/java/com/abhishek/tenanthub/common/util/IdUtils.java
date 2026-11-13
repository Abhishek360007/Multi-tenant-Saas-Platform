package com.abhishek.tenanthub.common.util;

import cn.hutool.core.util.RandomUtil;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * Twitter Modified snowflake ID algorithm
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/8/12 15:43
 */


@Slf4j
public class IdUtils {

    static {
        // workerId Range 1 to 63.
        var workerId = (short) RandomUtil.randomInt(1, 63);
        log.info("IdUtils random workerId: {}", workerId);
        var options = new IdGeneratorOptions(workerId);
        YitIdHelper.setIdGenerator(options);
    }


    /**
     * [Standalone] snowflake ID
     */
    public static long getStandaloneId() {
        return YitIdHelper.nextId();
    }


    /**
     * [Standalone] snowflake ID
     */
    public static String getStandaloneStrId() {
        return String.valueOf(YitIdHelper.nextId());
    }

}
