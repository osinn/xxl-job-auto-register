package com.xxl.job.plus.executor.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.plus.executor.annotation.XxlRegister;
import com.xxl.job.plus.executor.config.XxlJobAutoRegisterConfigProperties;
import com.xxl.job.plus.executor.model.XxlJobGroup;
import com.xxl.job.plus.executor.model.XxlJobInfo;
import com.xxl.job.plus.executor.service.JobGroupService;
import com.xxl.job.plus.executor.service.JobInfoService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author : Hydra
 * @date: 2022/9/20 9:57
 * @version: 1.0
 */
public class XxlJobAutoRegister implements ApplicationListener<ApplicationReadyEvent>,
        ApplicationContextAware {

    private static final Log log = LogFactory.get();

    private static final String XXL_JOB_AUTO_REGISTER_LOCK_KEY = "com.xxl.job.plus.executor.core.xxlJobAutoRegister";

    private ApplicationContext applicationContext;

    @Autowired
    private JobGroupService jobGroupService;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private XxlJobAutoRegisterConfigProperties xxlJobAutoRegisterConfigProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        boolean enabledDistributedLock = xxlJobAutoRegisterConfigProperties.isEnabledDistributedLock();
        if (!enabledDistributedLock) {
            //注册执行器
            addJobGroup();
            //注册任务
            addJobInfo();
        } else {
            // 需要配置redisson相关配置
            RedissonClient redissonClient = applicationContext.getBean(RedissonClient.class);
            RLock lock = redissonClient.getLock(XXL_JOB_AUTO_REGISTER_LOCK_KEY);
            try {
                lock.lock();
                //注册执行器
                addJobGroup();
                //注册任务
                addJobInfo();
            } finally {
                lock.unlock();
            }
        }
    }

    //自动注册执行器
    private void addJobGroup() {
        if (jobGroupService.preciselyCheck())
            return;

        if (jobGroupService.autoRegisterGroup())
            log.info("auto register xxl-job group success!");
    }

    private void addJobInfo() {
        List<XxlJobGroup> jobGroups = jobGroupService.getJobGroup();
        if (jobGroups.isEmpty()) {
            throw new RuntimeException("check auto register xxl-job group is success!");
        }
        XxlJobGroup xxlJobGroup = jobGroups.get(0);

        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            Map<Method, XxlJob> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    new MethodIntrospector.MetadataLookup<XxlJob>() {
                        @Override
                        public XxlJob inspect(Method method) {
                            return AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class);
                        }
                    });
            for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
                Method executeMethod = methodXxlJobEntry.getKey();
                XxlJob xxlJob = methodXxlJobEntry.getValue();

                //自动注册
                if (executeMethod.isAnnotationPresent(XxlRegister.class)) {
                    XxlRegister xxlRegister = executeMethod.getAnnotation(XxlRegister.class);
                    List<XxlJobInfo> jobInfo = jobInfoService.getJobInfo(xxlJobGroup.getId(), xxlJob.value());
                    XxlJobInfo registerXxlJobInfo = createXxlJobInfo(xxlJobGroup, xxlJob, xxlRegister);
                    if (!jobInfo.isEmpty()) {
                        //因为是模糊查询，需要再判断一次
                        Optional<XxlJobInfo> first = jobInfo.stream()
                                .filter(xxlJobInfo -> xxlJobInfo.getExecutorHandler().equals(xxlJob.value()))
                                .findFirst();
                        if (first.isPresent()) {
                            XxlJobInfo xxlJobInfo = first.get();
                            isUpdateXxlJobInfo(registerXxlJobInfo, xxlJobInfo);
                            continue;
                        }
                    }


                    ReturnT<Object> result = jobInfoService.addJobInfo(registerXxlJobInfo);
                    if (result.getCode() != ReturnT.SUCCESS_CODE) {
                        throw new RuntimeException(String.format("add jobInfo [%s]error! msg ======> %s", result.getCode(), result.getMsg()));
                    }
                }
            }
        }
    }

    private XxlJobInfo createXxlJobInfo(XxlJobGroup xxlJobGroup, XxlJob xxlJob, XxlRegister xxlRegister) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setJobGroup(xxlJobGroup.getId());
        xxlJobInfo.setJobDesc(xxlRegister.jobDesc());
        xxlJobInfo.setAuthor(xxlRegister.author());
        xxlJobInfo.setScheduleType("CRON");
        xxlJobInfo.setScheduleConf(xxlRegister.cron());
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(xxlJob.value());
        xxlJobInfo.setExecutorRouteStrategy(xxlRegister.executorRouteStrategy());
        xxlJobInfo.setMisfireStrategy("DO_NOTHING");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);
        xxlJobInfo.setGlueRemark("GLUE代码初始化");
        xxlJobInfo.setTriggerStatus(xxlRegister.triggerStatus());

        return xxlJobInfo;
    }

    private void isUpdateXxlJobInfo(XxlJobInfo registerXxlJobInfo, XxlJobInfo xxlJobInfo) {
        if (!xxlJobInfo.getScheduleConf().equals(registerXxlJobInfo.getScheduleConf())
                || !xxlJobInfo.getJobDesc().equals(registerXxlJobInfo.getJobDesc())
                || !xxlJobInfo.getAuthor().equals(registerXxlJobInfo.getAuthor())
                || !xxlJobInfo.getExecutorRouteStrategy().equals(registerXxlJobInfo.getExecutorRouteStrategy())
                || xxlJobInfo.getTriggerStatus() != registerXxlJobInfo.getTriggerStatus()
        ) {
            Integer triggerStatus = null;
            if (registerXxlJobInfo.getTriggerStatus() != xxlJobInfo.getTriggerStatus()) {
                triggerStatus = registerXxlJobInfo.getTriggerStatus();
            }
            BeanUtil.copyProperties(registerXxlJobInfo, xxlJobInfo, "id");
            if (triggerStatus != null) {
                if (triggerStatus == 1) {
                    jobInfoService.start(xxlJobInfo.getId());
                } else {
                    jobInfoService.stop(xxlJobInfo.getId());
                }
            }
            jobInfoService.update(xxlJobInfo);
        }
    }

}
