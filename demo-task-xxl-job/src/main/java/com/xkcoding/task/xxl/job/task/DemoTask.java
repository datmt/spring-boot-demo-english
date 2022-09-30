package com.xkcoding.task.xxl.job.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Test scheduled tasks
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-08-07 10:15
 */
@Slf4j
@Component
@JobHandler("demoTask")
public class DemoTask extends IJobHandler {

    /**
     * execute handler, invoked when executor receives a scheduling request
     *
     * @param param timing task parameters
     * @return Execution status
     * @throws Exception task exception
     */
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        You can dynamically get the parameters passed over, depending on the parameters, the currently scheduled task is different
        log.info("【param】= {}", param);
        XxlJobLogger.log("demo task run at : {}", DateUtil.now());
        return RandomUtil.randomInt(1, 11) % 2 == 0 ? SUCCESS : FAIL;
    }
}
