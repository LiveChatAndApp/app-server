package cn.wildfirechat.app.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 日期工具类
 *
 * @author huangxk
 * @version [1.0.0, 2016-9-2]
 */
public class DateUtils {

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";

    /**
     * yyyyMMddHHmmss
     */
    public static final String YMDHMS = "yyyyMMddHHmmss";

    /**
     * yyyyMMddHHmmss
     */
    public static final String yyyyMMddHHmmsss = "yyyyMMddHHmmsss";

    /**
     * yyyy-MM-dd HH:mm:ss.SSS
     */
    public static final String YMD_HMSS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * yyyyMMddHHmmssSSS
     */
    public static final String YMDHMSSS = "yyyyMMddHHmmssSSS";

    /**
     * yyyyMMdd
     */
    public static final String YMD = "yyyyMMdd";

    /**
     * yyyyMM
     */
    public static final String YM = "yyyyMM";

    /**
     * MMdd
     */
    public static final String MD = "MMdd";

    /**
     * MM-dd
     */
    public static final String MD_DASH = "MM-dd";

    /**
     * MM-dd HH:mm
     */
    public static final String MD_HM_DASH = "MM-dd HH:mm";

    /**
     * yyyy/MM/dd
     */
    public static final String YMD_SLASH = "yyyy/MM/dd";

    /**
     * yyyy-MM-dd
     */
    public static final String YMD_DASH = "yyyy-MM-dd";

    /**
     * yyyy-MM-dd H:m
     */
    public static final String YMD_DASH_WITH_TIME = "yyyy-MM-dd H:m";

    /**
     * yyyy-MM-dd HH:mm
     */
    public static final String DATETIME_YMD_DASH = "yyyy-MM-dd HH:mm";

    /**
     * yyyy/dd/MM
     */
    public static final String YDM_SLASH = "yyyy/dd/MM";

    /**
     * yyyy/MM/dd HH:mm:ss
     */
    public static final String YMD_SLAHMS = "yyyy/MM/dd HH:mm:ss";

    /**
     * HHmm
     */
    public static final String HM = "HHmm";

    /**
     * HHmmss
     */
    public static final String HMS = "HHmmss";

    /**
     * HH:mm
     */
    public static final String HM_COLON = "HH:mm";

    /**
     * HH:mm:ss
     */
    public static final String HMS_COLON = "HH:mm:ss";

    /**
     * yyyy-MM
     */
    public static final String YM_DASH = "yyyy-MM";

    /**
     * yyyy年MM月dd日
     */
    public static final String YMD_NYRSH = "yyyy年MM月dd日";

    /**
     * yyyy年MM月
     */
    public static final String YM_NYRSH = "yyyy年MM月";

    /**
     * MM月dd日
     */
    public static final String MD_NYRSH = "MM月dd日";

    public static final long YEAR = 24 * 60 * 60 * 365 * 1000L;

    public static final long DAY = 24 * 60 * 60 * 1000L;

    public static final long MINUTE = 60 * 1000L;

    public static final long SECOND = 1000L;

    private static final Map<String, DateFormat> DFS = new HashMap<>();

    /**
     * 获取当前时间的时间戳（精确到毫秒）
     * <p>
     * Description:获取当前时间的时间戳（精确到毫秒）<br />
     * </p>
     *
     * @return Long
     * @version 0.1 2013年10月30日
     */
    public static Long getCurrentTimeStamps() {
        return new Date().getTime();
    }

    /**
     * 将日期的字符串形式按格式转换成日期
     * <p>
     * Description:将日期的字符串形式按格式转换成日期<br />
     * </p>
     *
     * @param source  日期格式字符串
     * @param pattern 日期格式
     * @return Date
     * @version 0.1 2013年10月30日
     */
    public static Date parse(String source, String pattern) {
        if (source == null || source.length() == 0 || pattern == null) {
            return null;
        }
        Date date = null;
        try {
            // 中国标准时区
            if ("Asia/Shanghai".equalsIgnoreCase(TimeZone.getDefault().getID())) {
                date = new SimpleDateFormat(pattern, Locale.CHINA).parse(source);
            } else {
                date = new SimpleDateFormat(pattern).parse(source);
            }
        } catch (ParseException e) {
            System.out.println("error continue：" + source + " > " + pattern + "，错误：" + e.getMessage());
        }
        return date;
    }

    /**
     * 将当前时间按yyyy-MM-dd HH:mm:ss格式以字符串输出
     */
    public static String getNowFormat() {
        return getFormat(YMD_HMS).format(new Date());
    }

    /**
     * 按格式要求，将日期按字符串格式输出
     * <p>
     * Description:按格式要求，将日期按字符串格式输出<br />
     * </p>
     *
     * @param date    指定日期
     * @param pattern 日期格式
     * @return String
     * @version 0.1 2013年10月30日
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return getFormat(pattern).format(date);
    }

    /**
     * 按格式要求，按DateFormat格式输出
     * <p>
     * Description:按格式要求，按DateFormat格式输出<br />
     * </p>
     *
     * @return DateFormat
     * @version 0.1 2013年10月30日
     */
    public static DateFormat getFormat(String pattern) {
        DateFormat format = DFS.get(pattern);
        if (format == null) {
            format = new SimpleDateFormat(pattern);
            DFS.put(pattern, format);
        }
        return format;
    }

    /**
     * 获得当前时间，格式yyyy-MM-dd HH:mm:ss
     * <p>
     * Description:获得当前时间，按默认格式yyyy-MM-dd HH:mm:ss输出<br />
     * </p>
     *
     * @return String
     * @version 0.1 2013年10月30日
     */
    public static String getNow() {
        return getNow(YMD_HMS);
    }

    /**
     * 按格式要求，获得当前时间，按字符串格式输出
     * <p>
     * Description:按格式要求，获得当前时间，按字符串格式输出<br />
     * </p>
     *
     * @param pattern 日期格式
     * @return String
     * @version 0.1 2013年10月30日
     */
    public static String getNow(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 判断输入的年、月、日是否是有效日期
     * <p>
     * Description:判断输入的年、月、日是否是有效日期<br />
     * </p>
     *
     * @param year  年
     * @param month 月（1-12）
     * @param day   日（1-31）
     * @return boolean
     * @version 0.1 2013年10月30日
     */
    public static boolean isValid(int year, int month, int day) {
        if (month > 0 && month < 13 && day > 0 && day < 32) {
            // month of calendar is 0-based
            int mon = month - 1;
            Calendar calendar = new GregorianCalendar(year, mon, day);
            if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == mon &&
                    calendar.get(Calendar.DAY_OF_MONTH) == day) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将指定日期转换为日历格式的日期对象
     * <p>
     * Description:将指定日期转换为日历格式的日期对象<br />
     * </p>
     *
     * @param date 指定日期
     * @return Calendar
     * @version 0.1 2013年10月30日
     */
    private static Calendar convert(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 返回指定日期按年份【前移】或【后移】后的日期
     * <p>
     * Description:返回指定日期按年份【前移】或【后移】后的日期<br />
     * </p>
     *
     * @param date   指定日期
     * @param offset 当为正整数，则后移，当为负整数，则前移
     * @return Date
     * @version 0.1 2013年10月30日
     */
    public static Date addYears(Date date, int offset) {
        return offsetDate(date, Calendar.YEAR, offset);
    }

    /**
     * 返回指定日期按月份【前移】或【后移】后的日期
     * <p>
     * Description:返回指定日期按月份【前移】或【后移】后的日期<br />
     * </p>
     *
     * @param date   指定日期
     * @param offset 当为正整数，则后移，当为负整数，则前移
     * @return Date
     * @version 0.1 2013年10月30日
     */
    public static Date addMonths(Date date, int offset) {
        return offsetDate(date, Calendar.MONTH, offset);
    }

    /**
     * 返回指定日期按天数【前移】或【后移】后的日期
     * <p>
     * Description:返回指定日期按天数【前移】或【后移】后的日期<br />
     * </p>
     *
     * @param date   指定日期
     * @param offset 当为正整数，则后移，当为负整数，则前移
     * @return Date
     * @version 0.1 2013年10月30日
     */
    public static Date addDays(Date date, int offset) {
        return offsetDate(date, Calendar.DATE, offset);
    }

    /**
     * 返回指定日期按小时数【前移】或【后移】后的日期
     * <p>
     * Description:返回指定日期按小时数【前移】或【后移】后的日期<br />
     * </p>
     *
     * @param date   指定日期
     * @param offset 当为正整数，则后移，当为负整数，则前移
     * @return Date
     * @version 0.1 2013年10月30日
     */
    public static Date addHours(Date date, int offset) {
        return offsetDate(date, Calendar.HOUR, offset);
    }

    /**
     * 返回指定日期按分钟数【前移】或【后移】后的日期
     * <p>
     * Description:返回指定日期按分钟数【前移】或【后移】后的日期<br />
     * </p>
     *
     * @param date   指定日期
     * @param offset 当为正整数，则后移，当为负整数，则前移
     * @return Date
     * @version 0.1 2013年10月30日
     */
    public static Date addMinutes(Date date, int offset) {
        return offsetDate(date, Calendar.MINUTE, offset);
    }

    /**
     * 返回指定日期按秒数【前移】或【后移】后的日期
     * <p>
     * Description:返回指定日期按秒数【前移】或【后移】后的日期<br />
     * </p>
     *
     * @param date   指定日期
     * @param offset 当为正整数，则后移，当为负整数，则前移
     * @return Date
     * @version 0.1 2013年10月30日
     */
    public static Date addSeconds(Date date, int offset) {
        return offsetDate(date, Calendar.SECOND, offset);
    }

    /**
     * 返回指定日期按指定方式【前移】或【后移】后的日期
     * <p>
     * Description:返回指定日期按指定方式【前移】或【后移】后的日期<br />
     * </p>
     *
     * @param date   指定日期
     * @param field  指定方式，如：Calendar.YEAR 按年份，Calendar.MONTH 按月份，Calendar.DATE 按天数
     * @param offset 当为正整数，则后移，当为负整数，则前移
     * @return Date
     * @version 0.1 2013年10月30日
     */
    public static Date offsetDate(Date date, int field, int offset) {
        Calendar calendar = convert(date);
        calendar.add(field, offset);
        return calendar.getTime();
    }

    /**
     * 返回两个日期间的差异天数
     * <p>
     * Description:返回两个日期间的差异天数<br />
     * </p>
     *
     * @param date1 参照日期
     * @param date2 比较日期
     * @return 参照日期与比较日期之间的天数差异，正数表示参照日期在比较日期之后，0表示两个日期同天，负数表示参照日期在比较日期之前 int
     * @version 0.1 2013年10月30日
     */
    public static int dayDiff(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        return (int) (diff / DAY);
    }

    /**
     * <p>
     * Description:返回两个日期间的差异月数<br />
     * </p>
     *
     * @param date1 参照日期
     * @param date2 比较日期
     * @return int
     * @version 0.1 2015年3月13日
     */
    public static int monthDiff(Date date1, Date date2) {
        Calendar c1 = convert(date1);
        Calendar c2 = convert(date2);

        int years = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        int months = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);

        return years * 12 + months;
    }

    /**
     * 返回两个日期间的差异天数--方法2，按天计算；| 上面是按24小时的倍数计算的；
     * <p>
     * Description:返回两个日期间的差异天数<br />
     * </p>
     *
     * @param date1 参照日期
     * @param date2 比较日期
     * @return 参照日期与比较日期之间的天数差异，正数表示参照日期在比较日期之后，0表示两个日期同天，负数表示参照日期在比较日期之前 int
     * @version 0.1 2014年12月08日
     */
    public static int dayDiffByDay(Date date1, Date date2) {
        // 日期时间转换为年月日
        String startStr = DateUtils.format(date1, DateUtils.YMD_DASH);
        Date startDate = DateUtils.parse(startStr, DateUtils.YMD_DASH);

        String endStr = DateUtils.format(date2, DateUtils.YMD_DASH);
        Date endDate = DateUtils.parse(endStr, DateUtils.YMD_DASH);

        long diff = startDate.getTime() - endDate.getTime();
        return (int) (diff / DAY);
    }

    /**
     * 返回两个日期间的差异分钟数
     * <p>
     * Description:返回两个日期间的差异分钟数<br />
     * </p>
     *
     * @param date1 参照日期
     * @param date2 比较日期
     * @return 参照日期与比较日期之间的分钟数差异，正数表示参照日期在比较日期之后，0表示两个日期同分，负数表示参照日期在比较日期之前 int
     * @version 0.1 2013年10月30日
     */
    public static int minuteDiff(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        return (int) (diff / MINUTE);
    }

    /**
     * 返回两个日期间的差异秒数
     * <p>
     * Description:返回两个日期间的差异秒数<br />
     * </p>
     *
     * @param date1 参照日期
     * @param date2 比较日期
     * @return 参照日期与比较日期之间的秒数差异，正数表示参照日期在比较日期之后，0表示两个日期同分，负数表示参照日期在比较日期之前 int
     * @version 0.1 2013年10月30日
     */
    public static int secondDiff(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        return (int) (diff / SECOND);
    }

    /**
     * 获取一天的开始时间yyyy-MM-dd 00:00:00
     */
    public static Date getPureDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取一天的结束时间yyyy-MM-dd 23:59:59
     */
    public static Date getTailDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * <p>
     * Description:判断指定日期是本月的第几天 <br />
     * </p>
     *
     * @param dt 指定日期
     * @return int
     */
    public static int getMonthOfDateNumber(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * <p>
     * Description:得到指定日期所在月的天数<br />
     * </p>
     *
     * @param dt 指定日期
     * @return int
     */
    public static int getDaysOfMonth(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取本月的第一天
     *
     * @return (必须)
     */
    public static Date getFirstDayOfThisMonth() {
        return getFirstDayOfMonth(new Date());
    }

    /**
     * 获取指定日期所在月的第一天
     *
     * @return (必须)
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * 获取本月的最后一天
     *
     * @return (必须)
     */
    public static Date getLastDayOfThisMonth() {
        return getLastDayOfMonth(new Date());
    }

    /**
     * 获取指定日期所在月的最后一天
     *
     * @return (必须)
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * <p>
     * Description:获取指定日期是星期几<br />
     * </p>
     *
     * @param dt 指定日期
     * @return String
     * @version 0.1 2013年10月30日
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 返回指定日期是星期几的数字 1~7
     *
     * @MONDAY (星期一) return 1;
     * @TUESDAY (星期二) return 2;
     * @WEDNESDAY (星期三) return 3;
     * @THURSDAY (星期四) return 4;
     * @FRIDAY (星期五) return 5;
     * @SATURDAY (星期六) return 6
     * @SUNDAY (星期日) return 7;
     */
    public static int getWeekOfDateNumber(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w == 0) {
            w = 7;
        }
        return w;
    }

    /**
     * @SUNDAY (星期日) return 1;
     * @MONDAY (星期一) return 2;
     * @TUESDAY (星期二) return 3;
     * @WEDNESDAY (星期三) return 4;
     * @THURSDAY (星期四) return 5;
     * @FRIDAY (星期五) return 6;
     * @SATURDAY (星期六) return 7
     */
    public static int getNowDayOFWeek() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 得到本周周一
     */
    public static Date getMondayOfThisWeek() {
        return getMondayOfWeek(new Date());
    }

    /**
     * 得到指定日期所在周的周一
     */
    public static Date getMondayOfWeek(Date dt) {
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        c.add(Calendar.DATE, -day_of_week + 1);
        return c.getTime();
    }

    /**
     * 得到指定日期所在周的周日
     */
    public static Date getSundayOfThisWeek() {
        return getSundayOfWeek(new Date());
    }

    /**
     * 得到本周周日
     */
    public static Date getSundayOfWeek(Date dt) {
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        c.add(Calendar.DATE, -day_of_week + 7);
        return c.getTime();
    }

    /**
     * <p>
     * Description:获取指定日期是当年的第几周，如15表示当年第15周<br />
     * </p>
     *
     * @param dt 指定日期
     * @return int
     */
    public static int getWeekOfYear(Date dt) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(dt);
        int week = c.get(Calendar.WEEK_OF_YEAR) + 1;
        return week;
    }

    /**
     * <p>
     * Description:获取指定日期是当年的第几周，如201915表示2019年第15周<br />
     * </p>
     *
     * @param dt 指定日期
     * @return int
     */
    public static int getYearWeek(Date dt) {
        String year = DateUtils.format(getMondayOfWeek(dt), "yyyy");
        String week = String.format("%02d", getWeekOfYear(dt));
        return Integer.parseInt(year + week);
    }

    /**
     * 获取日期大的对象
     */
    public static Date getMax(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            return date1.getTime() > date2.getTime() ? date1 : date2;
        } else if (date1 != null) {
            return date1;
        } else {
            return date2;
        }
    }

    /**
     * 日期比较
     *
     * @param date1 日期1
     * @param date2 日期2
     */
    public static int compare(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return 0;
        }
        if (date1 == null) {
            return -1;
        }
        if (date2 == null) {
            return 1;
        }
        return Long.compare(date1.getTime(), date2.getTime());
    }

    /**
     * 尝试将日期字符串解析成日期对象
     * <p>该方法将依次尝试patterns中所有的格式，直到第一个成功为止。</p>
     * <p>该方法不会抛出异常，当所有的格式都解析失败将返回<code>null</code></p>
     * <p>linux系统下，日期格式可能不支持，解决：设置时区 或 设置linux系统语言</p>
     *
     * @param date     日期字符串
     * @param patterns 尝试的格式
     * @return 返回解析的日期对象，如果所有的格式都失败，则返回<code>null</code>
     */
    public static Date parseDate(String date, String... patterns) {
        if (date == null || date.length() == 0 || patterns.length == 0) {
            return null;
        }

        for (String ptn : patterns) {
            try {
                DateFormat format = getFormat(ptn);
                format.setLenient(false);
                return format.parse(date);
            } catch (Exception e) {
                System.out.println("error continue：" + date + " > " + ptn + "，错误：" + e.getMessage());
            }
        }

        return null;
    }

    /**
     * 两个日期是否为同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     */
    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定日期的小时数
     */
    public static int getHourOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 判断选择的日期是否是今天
     */
    public static boolean isToday(long time) {
        return isThisTime(time, YMD_DASH);
    }

    /**
     * 判断选择的日期是否是本周
     */
    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        return paramWeek == currentWeek;
    }

    /**
     * 判断选择的日期是否是本月
     */
    public static boolean isThisMonth(Date dt) {
        return isThisTime(dt.getTime(), YM_DASH);
    }

    /**
     * 判断选择的日期是否是当前时间
     */
    public static boolean isThisTime(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);
        String now = sdf.format(new Date());
        return param.equals(now);
    }

    /**
     * 判断选择的日期是否已跨日
     */
    public static boolean isCrossDay(Date date) {
        if (date == null) {
            date = new Date();
        }

        Calendar current = Calendar.getInstance();
        current.setTime(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        LocalDateTime currentTemporal = toLocalDateTime(current);
        LocalDateTime calendarTemporal = toLocalDateTime(calendar);

        Duration duration = Duration.between(currentTemporal, calendarTemporal);
        return duration.toDays() >= 1;
    }


    public static LocalDateTime toLocalDateTime(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        TimeZone tz = calendar.getTimeZone();
        ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
        return LocalDateTime.ofInstant(calendar.toInstant(), zid);
    }


}
