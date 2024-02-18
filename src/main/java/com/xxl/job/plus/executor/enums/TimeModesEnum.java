package com.xxl.job.plus.executor.enums;

/**
 * 描述
 *
 * @author wency_cai
 */
public enum TimeModesEnum {
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
     * 生成Cron表达式
     * - 每分钟：可选：10-59(单位秒)
     * - 每小时：可选：0-59(单位分钟)
     * - 每天：可选：小时、分钟
     * - 每周：可选：星期一到星期日、小时、分钟
     * - 每月：可选：1-31日、小时、分钟
     * - 每年：可选：1-12月、1-31日、小时、分钟
     *
     * @param minute     分钟
     * @param hour       小时
     * @param day        天
     * @param weekDay    周天
     * @param month      月
     * @param periodUnit 周期
     * @return
     */
    public static String generateCron(Integer minute,
                                      Integer hour,
                                      Integer day,
                                      Integer weekDay,
                                      Integer month,
                                      TimeModesEnum periodUnit) {
        switch (periodUnit) {
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
                return "0 */10 * * * ?";
        }
    }

    public static void main(String[] args) {
        // 每月执行，可选：1-31日、小时、分钟
        String cron = generateCron(0, 1, 1, null, null, TimeModesEnum.MONTH);
        System.out.println(cron);
    }
}
