package com.xkcoding.exception.handler.handler;

import com.xkcoding.exception.handler.exception.JsonException;
import com.xkcoding.exception.handler.exception.PageException;
import com.xkcoding.exception.handler.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * Unified exception handling
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-02 21:26
 */
@ControllerAdvice
@Slf4j
public class DemoExceptionHandler {
    private static final String DEFAULT_ERROR_VIEW = "error";

    /**
     * Unified json exception handling
     *
     * @param exception JsonException
     * @return Returns json format uniformly
     */
    @ExceptionHandler(value = JsonException.class)
    @ResponseBody
    public ApiResponse jsonErrorHandler(JsonException exception) {
        log.error("【JsonException】:{}", exception.getMessage());
        return ApiResponse.ofException(exception);
    }

    /**
     * Unified page exception handling
     *
     * @param exception PageException
     * @return Unified jump to exception page
     */
    @ExceptionHandler(value = PageException.class)
    public ModelAndView pageErrorHandler(PageException exception) {
        log.error("【DemoPageException】:{}", exception.getMessage());
        ModelAndView view = new ModelAndView();
        view.addObject("message", exception.getMessage());
        view.setViewName(DEFAULT_ERROR_VIEW);
        return view;
    }
}
