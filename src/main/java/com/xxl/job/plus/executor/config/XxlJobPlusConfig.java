package com.xxl.job.plus.executor.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.plus.executor.core.XxlJobAutoRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Hydra
 * @date: 2022/9/20 15:59
 * @version: 1.0
 */
@Configuration
@ComponentScan(basePackages = "com.xxl.job.plus.executor")
@EnableConfigurationProperties(XxlJobAutoRegisterConfigProperties.class)
@ConditionalOnProperty(value = XxlJobAutoRegisterConfigProperties.PREFIX + ".enable", havingValue = "true")
public class XxlJobPlusConfig {

    private Logger logger = LoggerFactory.getLogger(XxlJobPlusConfig.class);

    @Bean
    @ConditionalOnMissingBean(XxlJobSpringExecutor.class)
    public XxlJobSpringExecutor xxlJobExecutor(XxlJobAutoRegisterConfigProperties xxlJobAutoRegisterConfigProperties) {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobAutoRegisterConfigProperties.getAdmin().getAddresses());
        xxlJobSpringExecutor.setAppname(xxlJobAutoRegisterConfigProperties.getExecutor().getAppName());
        xxlJobSpringExecutor.setAddress(xxlJobAutoRegisterConfigProperties.getExecutor().getAddress());
        xxlJobSpringExecutor.setIp(xxlJobAutoRegisterConfigProperties.getExecutor().getIp());
        xxlJobSpringExecutor.setPort(xxlJobAutoRegisterConfigProperties.getExecutor().getPort());
        xxlJobSpringExecutor.setAccessToken(xxlJobAutoRegisterConfigProperties.getAccessToken());
        xxlJobSpringExecutor.setLogPath(xxlJobAutoRegisterConfigProperties.getExecutor().getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobAutoRegisterConfigProperties.getExecutor().getLogRetentionDays());
        return xxlJobSpringExecutor;
    }

    @Bean
    @ConditionalOnMissingBean(XxlJobAutoRegister.class)
    public XxlJobAutoRegister xxlJobAutoRegister() {
        return new XxlJobAutoRegister();
    }
}
