package com.xkcoding.template.beetl.controller;

import cn.hutool.core.util.ObjectUtil;
import com.xkcoding.template.beetl.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Homepage
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-10 11:17
 */
@Controller
@Slf4j
public class IndexController {

    @GetMapping(value = {"", "/"})
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();

        User user = (User) request.getSession().getAttribute("user");
        if (ObjectUtil.isNull(user)) {
            mv.setViewName("redirect:/user/login");
        } else {
            mv.setViewName("page/index.btl");
            mv.addObject(user);
        }

        return mv;
    }
}
