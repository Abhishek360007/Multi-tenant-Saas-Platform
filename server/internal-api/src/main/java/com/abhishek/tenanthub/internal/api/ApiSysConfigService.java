package com.abhishek.tenanthub.internal.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/14 10:17
 */

public interface ApiSysConfigService {


    /**
     * Look up a config value by configKey; returns String
     *
     * @param configKey
     * @return SysConfig
     * @author Wang Chen Chen
     * @date 2021/7/16 11:22
     */
    String getValueByKey(String configKey);


    /**
     * Look up a config value by configKey; returns List
     *
     * @param configKey
     * @return SysConfig
     * @author Wang Chen Chen
     * @date 2021/7/16 11:22
     */
    List<String> getValueListByKey(String configKey);


    /**
     * Look up a config value by configKey; returns Map
     *
     * @param configKey
     * @return SysConfig
     * @author Wang Chen Chen
     * @date 2021/7/16 11:22
     */
    Map<String, Object> getValueMapByKey(String configKey);


    /**
     * Look up a config value by configKey; returns Integer
     *
     * @param configKey
     * @return SysConfig
     * @author Wang Chen Chen
     * @date 2021/7/16 11:22
     */
    Integer getValueIntByKey(String configKey);


    /**
     * Look up a config value by configKey; returns Byte
     *
     * @param configKey
     * @return SysConfig
     * @author Wang Chen Chen
     * @date 2021/7/16 11:22
     */
    Byte getValueByteByKey(String configKey);



    /**
     * Look up a config value by configKey; returns Double
     *
     * @param configKey
     * @return SysConfig
     * @author Wang Chen Chen
     * @date 2021/7/16 11:22
     */
    Double getValueDoubleByKey(String configKey);


    /**
     * Look up a config value by configKey; returns LocalDateTime
     *
     * @param configKey
     * @return SysConfig
     * @author Wang Chen Chen
     * @date 2021/7/16 11:22
     */
    LocalDateTime getValueDateTimeByKey(String configKey);

    /**
     * Look up a config value by configKey; returns LocalTime
     *
     * @param configKey
     * @return SysConfig
     * @author Wang Chen Chen
     * @date 2021/7/16 11:22
     */
    LocalTime getValueTimeByKey(String configKey);

    /**
     * Look up a config value by configKey; returns LocalDate
     *
     * @param configKey
     * @return SysConfig
     * @author Wang Chen Chen
     * @date 2021/7/16 11:22
     */
    LocalDate getValueDateByKey(String configKey);

}
