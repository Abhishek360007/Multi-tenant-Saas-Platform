package com.abhishek.tenanthub.tenant.ds;

import java.lang.annotation.*;

/**
 * Data permission filter annotation
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * Department table alias
     */
    String deptAlias() default "d";

    /**
     * User table alias
     */
    String userAlias() default "u";

}