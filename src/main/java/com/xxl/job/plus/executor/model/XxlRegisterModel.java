package com.xxl.job.plus.executor.model;

/**
 * 服务接口注册参数
 *
 * @author wency_cai
 */
public class XxlRegisterModel {

    /**
     * cron表达式(必须)
     */
    private String cron;

    private String jobDesc = "default jobDesc";

    private String author = "default Author";

    /*
     * 默认为 ROUND 轮询方式
     * 可选： FIRST 第一个
     * */
    private String executorRouteStrategy = "ROUND";

    /**
     * 调度状态：0-停止，1-运行
     */
    private int triggerStatus;

    /***
     * 执行器，任务Handler名称(必须)
     */
    private String executorHandler;

    /**
     * 执行器名称(必须)
     */
    private String executorTitle;

    /**
     * 应用名称(必须)
     */
    private String appName;

    /**
     * 执行器，任务参数
     */
    private String executorParam;

    /**
     * 如果已经存在，是否跳过注册
     */
    private boolean skippingExist;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getExecutorRouteStrategy() {
        return executorRouteStrategy;
    }

    public void setExecutorRouteStrategy(String executorRouteStrategy) {
        this.executorRouteStrategy = executorRouteStrategy;
    }

    public int getTriggerStatus() {
        return triggerStatus;
    }

    public void setTriggerStatus(int triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public String getExecutorTitle() {
        return executorTitle;
    }

    public void setExecutorTitle(String executorTitle) {
        this.executorTitle = executorTitle;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getExecutorParam() {
        return executorParam;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam;
    }

    public boolean isSkippingExist() {
        return skippingExist;
    }

    public void setSkippingExist(boolean skippingExist) {
        this.skippingExist = skippingExist;
    }


}
