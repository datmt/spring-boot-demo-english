package com.xkcoding.ratelimit.guava.controller;

import cn.hutool.core.lang.Dict;
import com.xkcoding.ratelimit.guava.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Test
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-12 14:22
 */
@Slf4j
@RestController
public class TestController {

    @RateLimiter(value = 1.0, timeout = 300)
    @GetMapping("/test1")
    public Dict test1() {
        log.info("【test1】被执行了。。。。。");
        return Dict.create().set("msg", "hello,world!").set("description", "别想一直看到我，不信你快速刷新看看~");
    }

    @GetMapping("/test2")
    public Dict test2() {
        log.info("【test2】被执行了。。。。。");
        return Dict.create().set("msg", "hello,world!").set("description", "我一直都在，卟离卟弃");
    }

    @RateLimiter(value = 2.0, timeout = 300)
    @GetMapping("/test3")
    public Dict test3() {
        log.info("【test3】被执行了。。。。。");
        return Dict.create().set("msg", "hello,world!").set("description", "别想一直看到我，不信你快速刷新看看~");
    }
}
