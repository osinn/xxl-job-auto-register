package com.xxl.job.plus.executor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 描述
 *
 * @author wency_cai
 */
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobAutoRegisterConfigProperties {

    public final static String PREFIX = "xxl.job";

    /**
     * 是否使用内置注入配置
     */
    private boolean enable;

    /**
     * 是否启用分布式锁住自动配置，避免多台服务器并发自动注册
     * 需要配置 redisson 相关配置
     */
    private boolean enabledDistributedLock = false;

    /**
     * 调度中心部署根地址 [选填]：如调度中心集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册；
     */
    private AdminAddresses admin;

    /**
     * 执行器通讯TOKEN
     */
    private String accessToken;

    /**
     * 执行器
     */
    private Executor executor;

    public static class AdminAddresses {

        /**
         * 调度中心地址，集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册；
         */
        private String addresses;

        /**
         * 调度中心账号
         */
        private String username = "admin";

        /**
         * 调度中心密码
         */
        private String password = "123456";

        public String getAddresses() {
            return addresses;
        }

        public void setAddresses(String addresses) {
            this.addresses = addresses;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Executor {

        /**
         * 执行器AppName [选填]：执行器心跳注册分组依据；为空则关闭自动注册
         */
        private String appName;

        /**
         * 执行器注册 [选填]：优先使用该配置作为注册地址，为空时使用内嵌服务 ”IP:PORT“ 作为注册地址。从而更灵活的支持容器类型执行器动态IP和动态映射端口问题
         */
        private String address;

        /**
         * 执行器IP [选填]：默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯实用；地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"
         */
        private String ip;

        /**
         * 执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口
         */
        private int port = 9999;

        /**
         * 执行器运行日志文件存储磁盘路径 [选填] ：需要对该路径拥有读写权限；为空则使用默认路径
         */
        private String logPath;

        /**
         * 执行器日志文件保存天数 [选填] ： 过期日志自动清理, 限制值大于等于3时生效; 否则, 如-1, 关闭自动清理功能
         */
        private int logRetentionDays = -1;

        /**
         * 执行器名称
         */
        private String title;

        /*
         * 执行器地址类型：0=自动注册、1=手动录入
         * */
        private Integer addressType = 0;

        /*
         * 在上面为1的情况下，手动录入执行器地址列表，多地址逗号分隔
         * */
        private String addressList = "http://127.0.0.1:9999";

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getLogPath() {
            return logPath;
        }

        public void setLogPath(String logPath) {
            this.logPath = logPath;
        }

        public int getLogRetentionDays() {
            return logRetentionDays;
        }

        public void setLogRetentionDays(int logRetentionDays) {
            this.logRetentionDays = logRetentionDays;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getAddressType() {
            return addressType;
        }

        public void setAddressType(Integer addressType) {
            this.addressType = addressType;
        }

        public String getAddressList() {
            return addressList;
        }

        public void setAddressList(String addressList) {
            this.addressList = addressList;
        }
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnabledDistributedLock() {
        return enabledDistributedLock;
    }

    public void setEnabledDistributedLock(boolean enabledDistributedLock) {
        this.enabledDistributedLock = enabledDistributedLock;
    }

    public AdminAddresses getAdmin() {
        return admin;
    }

    public void setAdmin(AdminAddresses admin) {
        this.admin = admin;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
