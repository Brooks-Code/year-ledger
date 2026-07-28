package com.brooks.mall.user.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/7/22 14:23
 */
public class TimeUtil {
    /**
     * 时间格式
     */
    public static final SimpleDateFormat ICAL_FORMAT;
    private static final TimeZone SHANGHAI_TZ = TimeZone.getTimeZone("Asia/Shanghai");

    static {
        ICAL_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ICAL_FORMAT.setTimeZone(SHANGHAI_TZ);
    }
    /**
     * 设置时间日期
     */
    public static String getMySetTime(Date OriginalTime) {
        String ReturnTimeStr = "";
        if (OriginalTime != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(OriginalTime);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            ReturnTimeStr = ICAL_FORMAT.format(cal.getTime());
        }
        return ReturnTimeStr;
    }
}
