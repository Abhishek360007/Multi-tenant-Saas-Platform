package com.abhishek.tenanthub.common.util;

import com.abhishek.tenanthub.common.domain.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * RESTful API Unified response entity
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */

@Getter
@Setter
@Schema(description = "Generic response")
public class JsonResult<T> implements Serializable {

    private static final int SUCCESS = 200;

    private static final int FAIL = 100;

    /**
     * Status code
     */
    @Schema(description = "Status code 200: success, 100: failure, anything else: error")
    private Integer status;

    /**
     * Response message
     */
    @Schema(description = "Message")
    private String message;

    /**
     * Response content
     */
    @Schema(description = "Result")
    private T data;

    private JsonResult() {
    }

    /**
     * Custom status code
     *
     * @param status
     * @param message
     * @param data
     * @return JsonResult
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    private static <T> JsonResult<T> result(int status, String message, T data) {
        var result = new JsonResult<T>();
        result.status = status;
        result.message = message;
        result.data = data;
        return result;
    }

    /**
     * Custom status code
     *
     * @param status
     * @param data
     * @return JsonResult
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    private static <T> JsonResult<T> result(int status, T data) {
        return result(status, "ok", data);
    }

    /**
     * Custom status code [not recommended; add a helper method instead]
     *
     * @param status
     * @param message
     * @return JsonResult
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static <T> JsonResult<String> result(int status, String message) {
        return result(status, message, null);
    }


    /**
     * Done
     *
     * @return JsonResult
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static <T> JsonResult<String> success() {
        return result(SUCCESS, "ok");
    }

    /**
     * Done
     *
     * @param total
     * @param list
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static <T> JsonResult<Pagination<T>> success(long total, List<T> list) {
        return success(new Pagination<T>(total, list));
    }


    /**
     * Done
     *
     * @param message
     * @return JsonResult
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static <T> JsonResult<String> success(String message) {
        return result(SUCCESS, message, null);
    }

    /**
     * Done
     *
     * @param data
     * @return JsonResult
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static <T> JsonResult<T> success(T data) {
        return result(SUCCESS, "ok", data);
    }

    /**
     * Done
     *
     * @param message
     * @param data
     * @return JsonResult
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static <T> JsonResult<T> success(String message, T data) {
        return result(SUCCESS, message, data);
    }


    /**
     * Operation failed
     *
     * @param message
     * @return JsonResult
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static <T> JsonResult<String> fail(String message) {
        return result(FAIL, message, null);
    }

    /**
     * Operation failed
     *
     * @param message
     * @return JsonResult
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static <T> JsonResult<T> fail(String message, Class<T> data) {
        return result(FAIL, message, null);
    }


    /**
     * Operation failed
     *
     * @param message
     * @return JsonResult
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static <T> JsonResult<T> fail(String message, T data) {
        return result(FAIL, message, null);
    }


    /**
     * Operation failed
     *
     * @param message
     * @return JsonResult
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static <T> JsonResult<T> fail2(String message, T data) {
        return result(FAIL, message, data);
    }

    /**
     * Error
     *
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static <T> JsonResult<String> error(int status, String message) {
        return result(status, message, null);
    }


    /**
     * Error
     *
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static <T> JsonResult<T> error(int status, String message, Class<T> data) {
        return result(status, message, null);
    }


    /**
     * Get the success constant
     *
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static int getSuccess() {
        return SUCCESS;
    }

    /**
     * Get the failure constant
     *
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/12 21:08
     */
    public static int getFail() {
        return FAIL;
    }

}
