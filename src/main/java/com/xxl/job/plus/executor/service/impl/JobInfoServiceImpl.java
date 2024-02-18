package com.xxl.job.plus.executor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.xxl.job.plus.executor.model.XxlJobGroup;
import com.xxl.job.plus.executor.model.XxlJobInfo;
import com.xxl.job.plus.executor.model.XxlRegisterModel;
import com.xxl.job.plus.executor.service.JobGroupService;
import com.xxl.job.plus.executor.service.JobInfoService;
import com.xxl.job.plus.executor.service.JobLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : Hydra
 * @date: 2022/9/20 10:36
 * @version: 1.0
 */
@Service
public class JobInfoServiceImpl implements JobInfoService {

    private static final Log log = LogFactory.get();

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Autowired
    private JobLoginService jobLoginService;

    @Autowired
    private JobGroupService jobGroupService;

    @Override
    public List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler) {
        String url = adminAddresses + "/jobinfo/pageList";
        HttpResponse response = HttpRequest.post(url)
                .form("jobGroup", jobGroupId)
                .form("executorHandler", executorHandler)
                .form("triggerStatus", -1)
                .cookie(jobLoginService.getCookie())
                .execute();

        String body = response.body();
        JSONArray array = JSONUtil.parse(body).getByPath("data", JSONArray.class);
        List<XxlJobInfo> list = array.stream()
                .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobInfo.class))
                .collect(Collectors.toList());

        return list;
    }

    @Override
    public Integer addJobInfo(XxlJobInfo xxlJobInfo) {
        String url = adminAddresses + "/jobinfo/add";
        Map<String, Object> paramMap = BeanUtil.beanToMap(xxlJobInfo);
        HttpResponse response = HttpRequest.post(url)
                .form(paramMap)
                .cookie(jobLoginService.getCookie())
                .execute();
        String body = response.body();
        JSON json = JSONUtil.parse(response.body());
        Object code = json.getByPath("code");
        if (code.equals(200)) {
            return Convert.toInt(json.getByPath("content"));
        }
        throw new RuntimeException("add jobInfo error! ======>" + body);
    }

    @Override
    public void update(XxlJobInfo xxlJobInfo) {
        String url = adminAddresses + "/jobinfo/update";
        Map<String, Object> paramMap = BeanUtil.beanToMap(xxlJobInfo);
        HttpResponse response = HttpRequest.post(url)
                .form(paramMap)
                .cookie(jobLoginService.getCookie())
                .execute();
        String body = response.body();
        JSON json = JSONUtil.parse(response.body());
        Object code = json.getByPath("code");
        if (!code.equals(200)) {
            throw new RuntimeException("update jobInfo error! =====>" + body);
        }
    }

    @Override
    public void start(int id) {
        String url = adminAddresses + "/jobinfo/start";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        HttpResponse response = HttpRequest.post(url)
                .form(paramMap)
                .cookie(jobLoginService.getCookie())
                .execute();
        String body = response.body();
        JSON json = JSONUtil.parse(response.body());
        Object code = json.getByPath("code");
        if (!code.equals(200)) {
            throw new RuntimeException("update jobInfo error! =====>" + body);
        }
    }

    @Override
    public void stop(int id) {
        String url = adminAddresses + "/jobinfo/stop";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        HttpResponse response = HttpRequest.post(url)
                .form(paramMap)
                .cookie(jobLoginService.getCookie())
                .execute();
        String body = response.body();
        JSON json = JSONUtil.parse(response.body());
        Object code = json.getByPath("code");
        if (!code.equals(200)) {
            throw new RuntimeException("update jobInfo error! =====>" + body);
        }
    }

    @Override
    public void remove(int id) {
        this.stop(id);
        String url = adminAddresses + "/jobinfo/remove";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        HttpResponse response = HttpRequest.post(url)
                .form(paramMap)
                .cookie(jobLoginService.getCookie())
                .execute();
        String body = response.body();
        JSON json = JSONUtil.parse(response.body());
        Object code = json.getByPath("code");
        if (!code.equals(200)) {
            throw new RuntimeException("update jobInfo error! =====>" + body);
        }
    }

    @Override
    public List<XxlJobInfo> xxlJobRegister(List<XxlRegisterModel> xxlRegisterModelList) {
        if (xxlRegisterModelList == null || xxlRegisterModelList.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, Set<XxlRegisterModel>> setMap = new HashMap<>();
        for (XxlRegisterModel xxlRegisterModel : xxlRegisterModelList) {
            String key = xxlRegisterModel.getAppName() + "," + xxlRegisterModel.getExecutorTitle();
            Set<XxlRegisterModel> xxlRegisterModels = setMap.computeIfAbsent(key, k -> new HashSet<>());
            xxlRegisterModels.add(xxlRegisterModel);
        }

        List<XxlJobInfo> xxlJobInfoList = new ArrayList<>();

        for (Map.Entry<String, Set<XxlRegisterModel>> xxlRegisterModelSet : setMap.entrySet()) {
            String key = xxlRegisterModelSet.getKey();
            Set<XxlRegisterModel> xxlRegisterList = xxlRegisterModelSet.getValue();
            String[] split = key.split(",");
            XxlJobGroup jobGroup = jobGroupService.getJobGroup(split[0], split[1]);
            if (jobGroup == null) {
                throw new RuntimeException("not found xxlJobGroup");
            }

            for (XxlRegisterModel xxlRegisterModel : xxlRegisterList) {
                XxlJobInfo xxlJobInfo = createXxlJobInfo(xxlRegisterModel, jobGroup);
                if (xxlJobInfo != null) {
                    xxlJobInfoList.add(xxlJobInfo);
                }
            }
        }

        for (XxlJobInfo xxlJobInfo : xxlJobInfoList) {
            Integer id = this.addJobInfo(xxlJobInfo);
            xxlJobInfo.setId(id);
        }
        return xxlJobInfoList;
    }

    private XxlJobInfo createXxlJobInfo(XxlRegisterModel xxlRegisterModel, XxlJobGroup jobGroup) {
        List<XxlJobInfo> jobInfo = this.getJobInfo(jobGroup.getId(), xxlRegisterModel.getExecutorHandler());
        Optional<XxlJobInfo> first = jobInfo.stream()
                .filter(xxlJobInfo -> xxlJobInfo.getExecutorHandler().equals(xxlRegisterModel.getExecutorHandler()))
                .findFirst();
        if (first.isPresent()) {
            log.warn(jobGroup.getAppname() + "<--->" + jobGroup.getTitle() + "<--->" + xxlRegisterModel.getExecutorHandler() + " skipping exists");
            return null;
        }

        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setJobGroup(jobGroup.getId());
        xxlJobInfo.setJobDesc(xxlRegisterModel.getJobDesc());
        xxlJobInfo.setAuthor(xxlRegisterModel.getAuthor());
        xxlJobInfo.setScheduleType("CRON");
        xxlJobInfo.setScheduleConf(xxlRegisterModel.getCron());
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(xxlRegisterModel.getExecutorHandler());
        xxlJobInfo.setExecutorRouteStrategy(xxlRegisterModel.getExecutorRouteStrategy());
        xxlJobInfo.setMisfireStrategy("DO_NOTHING");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);
        xxlJobInfo.setGlueRemark("GLUE代码初始化");
        xxlJobInfo.setTriggerStatus(xxlRegisterModel.getTriggerStatus());
        xxlJobInfo.setExecutorParam(xxlRegisterModel.getExecutorParam());
        return xxlJobInfo;
    }
}
