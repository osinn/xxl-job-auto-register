package com.xxl.job.plus.executor.enums;

import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ┌──────────── [可选] 秒 (0 - 59)
 * | ┌────────── 分钟 (0 - 59)
 * | | ┌──────── 小时 (0 - 23)
 * | | | ┌────── 天数 (1 - 31)
 * | | | | ┌──── 月份 (1 - 12) OR jan,feb,mar,apr ...
 * | | | | | ┌── 星期几 (0 - 6, 星期天 = 0) OR sun,mon ...
 * | | | | | |
 * * * * * * * 命令
 *
 * @author wency_cai
 */
public enum TimeModesEnum {

    SECOND("秒"),
    MINUTE("分钟"),
    HOUR("小时"),
    DAY("天"),
    WEEK("周"),
    MONTH("月"),
    YEAR("年");

    private final String text;

    TimeModesEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    /**
     * 生成Cron表达式,不校验是否合法
     * 时间模式(单选)：
     * 秒、分钟、小时、天、周、月、年
     * 选择模式
     * 秒  ->  只能输入 second(例如：second = 1，每秒执行)
     * 分  ->  只能输入 minute(例如：minute = 1，每分钟执行)
     * 时  ->  只能输入 minute(例如：minute = 2，每小时2分执行)
     * 天  ->  只能输入 hour、minute(例如：hour = 1，minute = 2，每天凌晨 1点2分执行)
     * 周  ->  只能输入 weekDay、hour、minute。weekDay范围 1-7 (例如：weekDay = 1，hour = 1，minute = 2，每周一凌晨1点2分执行)
     * 月  ->  只能输入 day、hour、minute (例如：day = 1，hour = 1，minute = 2，每月1号凌晨1点2分执行)
     * 年  ->  只能输入 month、day、hour、minute (例如：month = 2、day = 1，hour = 1，minute = 2，每年2月1号凌晨1点2分执行)
     *
     * @param second     秒
     * @param minute     分钟
     * @param hour       小时
     * @param day        天
     * @param weekDay    周天
     * @param month      月
     * @param periodUnit 周期
     * @return
     */
    public static String generateCronExpression(Integer second,
                                                Integer minute,
                                                Integer hour,
                                                Integer day,
                                                Integer weekDay,
                                                Integer month,
                                                TimeModesEnum periodUnit) {
        switch (periodUnit) {
            case SECOND:
                return "0/" + (second != null ? second : 1) + " * * * * ?";
            case MINUTE:
                return "0 */" + (minute != null ? minute : 0) + " * * * ?";
            case HOUR:
                return "0 " + (minute != null ? minute : 0) + " * * * ?";
            case DAY:
                return "0 " + (minute != null ? minute : 0) + " " + (hour != null ? hour : 0) + " * * ?";
            case WEEK:
                return "0 " + (minute != null ? minute : 0) + " " + (hour != null ? hour : 0) + " ? * " + (weekDay != null ? weekDay : 0);
            case MONTH:
                return "0 " + (minute != null ? minute : 0) + " " + (hour != null ? hour : 0) + " " + (day != null ? day : 0) + " * ?";
            case YEAR:
                return "0 " + (minute != null ? minute : 0) + " " + (hour != null ? hour : 0) + " " + (day != null ? day : 0) + " " + (month != null ? month : 0) + " ?";
            default:
                throw new RuntimeException("cron生成表达式 period unit error");
        }
    }

    /**
     * 生成Cron表达式,校验是否合法，并抛出异常
     * 时间模式(单选)：
     * 秒、分钟、小时、天、周、月、年
     * 选择模式
     * 秒  ->  只能输入 second(例如：second = 1，每秒执行)
     * 分  ->  只能输入 minute(例如：minute = 1，每分钟执行)
     * 时  ->  只能输入 minute(例如：minute = 2，每小时2分执行)
     * 天  ->  只能输入 hour、minute(例如：hour = 1，minute = 2，每天凌晨 1点2分执行)
     * 周  ->  只能输入 weekDay、hour、minute。weekDay范围 1-7 (例如：weekDay = 1，hour = 1，minute = 2，每周一凌晨1点2分执行)
     * 月  ->  只能输入 day、hour、minute (例如：day = 1，hour = 1，minute = 2，每月1号凌晨1点2分执行)
     * 年  ->  只能输入 month、day、hour、minute (例如：month = 2、day = 1，hour = 1，minute = 2，每年2月1号凌晨1点2分执行)
     *
     * @param second     秒
     * @param minute     分钟
     * @param hour       小时
     * @param day        天
     * @param weekDay    周天
     * @param month      月
     * @param periodUnit 周期
     * @return
     */
    public static String generateCronExpressionValid(Integer second,
                                                     Integer minute,
                                                     Integer hour,
                                                     Integer day,
                                                     Integer weekDay,
                                                     Integer month,
                                                     TimeModesEnum periodUnit) {
        String cronExpression = generateCronExpression(second, minute, hour, day, weekDay, month, periodUnit);
        if (isValidExpression(cronExpression)) {
            return cronExpression;
        }

        throw new RuntimeException("时间配置不错误");
    }

    /**
     * 校验表达式
     *
     * @param cron 表达式
     * @return true-合法，false-不合法
     */
    public static boolean isValidExpression(String cron) {
        return CronExpression.isValidExpression(cron);
    }

    /**
     * 获取表达式最近5次运行时间
     *
     * @param expression cron表达式
     * @return 返回最近5次运行时间
     */
    public static List<String> getRecentRunningTime(String expression) {
        return getRecentRunningTime(expression, 5);
    }

    /**
     * 获取表达式最近N次运行时间
     *
     * @param numb       最近N次
     * @param expression cron表达式
     * @return 返回最近N次运行时间
     */
    public static List<String> getRecentRunningTime(String expression, int numb) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        List<String> runningTimeList = new ArrayList<>();
        for (int i = 0; i < numb; i++) {
            date = getNextRunDate(expression, date);
            runningTimeList.add(dateFormat.format(date));
        }
        return runningTimeList;
    }

    private static Date getNextRunDate(String expression, Date date) {
        return new CronTrigger(expression).nextExecutionTime(new SimpleTriggerContext(date, date, date));
    }

    public static void main(String[] args) {
        // 0 30 11 ? * 2
        // 只能输入 weekDay、hour、minute。weekDay范围 1-7 (例如：weekDay = 1，hour = 1，minute = 2，每周一凌晨1点2分执行)
        String cron = generateCronExpression(null, 30, 11, null, 7, null, TimeModesEnum.WEEK);
        System.out.println(cron);
        for (String s : getRecentRunningTime("0 30 11 ? * 2")) {
            System.out.println(s);
        }
//        for (String date : getRecentRunningTime(cron)) {
//            System.out.println(date);
//        }
    }
}
