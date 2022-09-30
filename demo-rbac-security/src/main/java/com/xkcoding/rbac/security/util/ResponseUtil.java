package com.xkcoding.rbac.security.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xkcoding.rbac.security.common.ApiResponse;
import com.xkcoding.rbac.security.common.BaseException;
import com.xkcoding.rbac.security.common.IStatus;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * Response generic tool class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 17:37
 */
@Slf4j
public class ResponseUtil {

    /**
     * Write json to Response
     *
     * @param response response
     * @param status status
     * @param data returns data
     */
    public static void renderJson(HttpServletResponse response, IStatus status, Object data) {
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(200);

            FIXME: BUG for hutool: JSONUtil.toJsonStr()
            When converting JSON to String, there is an error in the String converted to String when null values are ignored
            response.getWriter().write(JSONUtil.toJsonStr(new JSONObject(ApiResponse.ofStatus(status, data), false)));
        } catch (IOException e) {
            log.error("Response写出JSON异常，", e);
        }
    }

    /**
     * Write json to Response
     *
     * @param response response
     * @param exception exception
     */
    public static void renderJson(HttpServletResponse response, BaseException exception) {
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(200);

            FIXME: BUG for hutool: JSONUtil.toJsonStr()
            When converting JSON to String, there is an error in the String converted to String when null values are ignored
            response.getWriter().write(JSONUtil.toJsonStr(new JSONObject(ApiResponse.ofException(exception), false)));
        } catch (IOException e) {
            log.error("Response写出JSON异常，", e);
        }
    }
}
