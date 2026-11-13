package com.abhishek.tenanthub.web.repeat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO Duplicate submission annotation
 *
 * @author WangChenChen
 * @date 2023/10/31 19:30
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {

    /**
     * Default expiry 1500 ms
     */
    long value() default 1500;

}
