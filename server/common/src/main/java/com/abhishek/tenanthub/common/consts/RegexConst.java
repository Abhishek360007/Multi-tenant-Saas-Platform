package com.abhishek.tenanthub.common.consts;

/**
 * <p>
 * Regular expression constants
 * </p>
 *
 * @author WangChenChen
 * @version 1.0.0
 * @date 2024/1/10 9:52
 */

public interface RegexConst {

    // Indian mobile number: 10 digits starting 6-9, optional +91 or 0 prefix
    String MOBILE = "^(?:\\+91[\\-\\s]?|0)?[6-9]\\d{9}$";

    // Indian mobile number, or landline with STD code (e.g. 080-41234567)
    String TEL_PHONE = "^(?:\\+91[\\-\\s]?|0)?[6-9]\\d{9}$|^0\\d{2,4}[\\-\\s]?\\d{6,8}$";

}
