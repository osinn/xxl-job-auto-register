package com.xxl.job.plus.executor.service;

import com.xxl.job.plus.executor.model.XxlJobInfo;
import com.xxl.job.plus.executor.model.XxlRegisterModel;

import java.util.List;

public interface JobInfoService {

    List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler);

    Integer addJobInfo(XxlJobInfo xxlJobInfo);

    void update(XxlJobInfo xxlJobInfo);

    void start(int id);

    void stop(int id);

    void remove(int id);

    /**
     * 注册任务调度
     *
     * @param xxlRegisterModelList 注册信息
     * @return 返回任务信息
     */
    List<XxlJobInfo> xxlJobRegister(List<XxlRegisterModel> xxlRegisterModelList);

}
