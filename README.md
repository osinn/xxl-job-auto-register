## xxljob-autoregister-spring-boot-starter
> 在https://github.com/trunks2008/xxl-job-auto-register基础上添加新功能
**********************************

- 自动注册xxl-job执行器以及任务
- 服务启动获取@XxlRegister参数，如果xxl-job中不存在任务，则自动注册，如果已存在，则根据变化做更新
- 加入redisson 可配置是否开启分布式锁自动注册

## 1、打包

```
mvn clean install
```

## 2、项目中引入

```xml
<dependency>
    <groupId>io.github.osinn</groupId>
    <artifactId>xxl-job-auto-register</artifactId>
    <version>0.0.1</version>
</dependency>

<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>${hutool.version}</version>
</dependency>
```

## 3、配置

springboot项目配置文件application.properties：

```properties
server.port=8082

# 原生xxl-job配置
xxl.job.admin.addresses=http://127.0.0.1:8080/xxl-job-admin
xxl.job.accessToken=default_token
xxl.job.executor.appname=xxl-job-executor-test
xxl.job.executor.address=
xxl.job.executor.ip=127.0.0.1
xxl.job.executor.port=9999
xxl.job.executor.logpath=/data/applogs/xxl-job/jobhandler
xxl.job.executor.logretentiondays=30

# 新增配置项，必须项
# admin用户名
xxl.job.admin.username=admin
# admin 密码
xxl.job.admin.password=123456
# 执行器名称
xxl.job.executor.title=Exe-Titl

# 新增配置项，可选项
# 执行器地址类型：0=自动注册、1=手动录入，默认为0
xxl.job.executor.addressType=1
# 在上面为1的情况下，手动录入执行器地址列表，多地址逗号分隔
xxl.job.executor.addressList=http://127.0.0.1:9999
```
- application.yml
```
xxl:
  job:
    # 启用xxl-job配置，否则需要自行配置注入XxlJobSpringExecutor
    enable: true
    # 是否启用分布式锁住自动配置，避免多台服务器并发自动注册，依赖 redisson
    enabled-distributed-lock: true
    accessToken: default_token
    admin:
      addresses: http://127.0.0.1:8080/xxl-job-admin
      password: 123456
      username: admin
    executor:
      address: ''
      address-list: http://127.0.0.1:9999
      addressType: 1
      app-name: xxl-job-executor-test
      ip: 127.0.0.1
      log-path: /data/applogs/xxl-job/jobhandler
      log-retention-days: 30
      port: 9999
      title: Exe-Titl
```

`XxlJobSpringExecutor`参数配置与之前相同

## 4、添加注解
需要自动注册的方法添加注解`@XxlRegister`，不加则不会自动注册

```java
@Service
public class TestService {

    @XxlJob(value = "testJob")
    @XxlRegister(cron = "0 0 0 * * ? *",
            author = "hydra",
            jobDesc = "测试job")
    public void testJob(){
        System.out.println("#公众号：码农参上");
    }


    @XxlJob(value = "testJob222")
    @XxlRegister(cron = "59 1-2 0 * * ?",
            triggerStatus = 1)
    public void testJob2(){
        System.out.println("#作者：Hydra");
    }

    @XxlJob(value = "testJob444")
    @XxlRegister(cron = "59 59 23 * * ?")
    public void testJob4(){
        System.out.println("hello xxl job");
    }
}
```
## 5、注入JobInfoService Bean动态管理任务调度
