package cn.sylen.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @description
 * @author ming
 * @2013-7-2 上午11:13:52
 */
public class DateTimeUtil {
    private static Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);
    public static final String PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DAY = "yyyy-MM-dd";
    public static final String PATTERN_COMPACT = "yyyyMMdd";
    public static final String PATTERN_DAY_SLASH = "yyyy/MM/dd";
    public static final String PATTERN_MONTH_SLASH = "yyyy/MM";
    
    public static SimpleDateFormat formatDisplayDate = new SimpleDateFormat("M月d号");
    public static SimpleDateFormat formatDisplayTimeA = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    
    public static Date today(){
        return new Date();
    }
    
    public static String now(){
        return formatDate(today());
    }
    
    public static String formatDate(Date date){
        return formatDate(date,PATTERN_DEFAULT);
    }
    
    public static String formatDate(Date date, String pattern) {
        if (date == null) throw new IllegalArgumentException("date is null");
        if (pattern == null) throw new IllegalArgumentException("pattern is null");
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }
    /**
     * 生成商机app展示的时间
     */
    public static String businessAppDisplayTime(Timestamp timestamp) {
        if(timestamp == null) {
            return "";
        }
        Date date = new Date(timestamp.getTime());
        Date date1 = getMorningDate(date);
        long time = date1.getTime();

        long now = System.currentTimeMillis();
        int eclapseSec = (int) ((now - time) / 1000);
        int days = (int) (eclapseSec / (24 * 60 * 60));

        StringBuffer sb = new StringBuffer();
        if(days > 0) {
            if(days == 1) {
                sb.append("昨天 "+parseDateTime(timestamp, "HH:mm"));
            } else if(days == 2){
                sb.append("前天 "+parseDateTime(timestamp, "HH:mm"));
            } else {
                sb.append(parseDateTime(timestamp, "yyyy/MM/dd"));
            }
        } else {
            sb.append("今天 "+parseDateTime(timestamp, "HH:mm"));
        }
        return sb.toString();
    }


    /**
     * 生成app展示的时间
     */
    public static String generateAppDisplayTime(Timestamp timestamp) {
        if(timestamp == null) {
            return "";
        }
        long time = timestamp.getTime();
        long now = System.currentTimeMillis();
        int eclapseSec = (int) ((now - time) / 1000);
        int days = (int) (eclapseSec / (24 * 60 * 60));

        StringBuffer sb = new StringBuffer();
        if(days > 0) {
            if(days == 1) {
                sb.append("昨天");
            } else if(days == 2){
                sb.append("前天");
            } else if(days>=30){
                int month=days/30;
                if(month>12){
                    sb.append(normalizeTime(time));
                }else {
                    sb.append(month).append("个月前");
                }

            }else if(days < 30) {
                sb.append(days).append("天前");
            }
        } else {
            sb.append(parseDateTime(timestamp, "HH:mm"));
        }
        return sb.toString();
    }

    public static Date getDaysAgo(int days) {
        return new Date(System.currentTimeMillis() - days * 86400 * 1000l);
    }

    /**
     * 获得可以正常显示的时间
     * @param time
     * @return
     */
    public static String normalizeTime(long time) {
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return sm.format(date);
    }

    public static String normalizeTime(Timestamp timestamp) {
        if(timestamp==null){
            return "";
        }
        Date date = new Date(timestamp.getTime());
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sm.format(date);
    }

    /**
     * 获得可以正常显示的时间
     * @param date"yyyy-MM-dd"
     * @return
     */
    public static String parseDateTime(Date date,String format) {
        if(date==null){
            return "";
        }
        SimpleDateFormat sm = new SimpleDateFormat(format);
        return sm.format(date);
    }


    /**
     * 获取今天凌晨的时间
     */
    public static Date getTodayDate() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getMorningDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取昨天凌晨的时间
     */
    public static Date getYesterdayDate() {
        Date date = new Date(System.currentTimeMillis() - 86400 * 1000L);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    //获取当天的年，月，日
    public static String getDateByFormat(Date date,String format){
        if(date == null){
            date=new Date();
        }

        SimpleDateFormat formatDate = new SimpleDateFormat(format);
        return formatDate.format(date);

    }

    //计算两个时间相差的年份
    public static long getDateDiff(Date dateStart,Date dateStop){
        if(dateStart!=null && dateStop!=null){
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy");
            formatDate.format(dateStart);
            formatDate.format(dateStop);
            long diff = (dateStart.getTime()-dateStop.getTime())/1000/3600/24/365;
            return diff;
        }
        return 0;
    }

    /**
     * 字符串转为Date
     * @param date"yyyy-MM-dd"
     * @return
     */
    public static Date parseStringToDate(String date,String format) {
        if(date==null){
            return null;
        }
        SimpleDateFormat sm = new SimpleDateFormat(format);
        try {
            return sm.parse(date);
        } catch (ParseException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }


    public static Date parseStringToDate(String time) {
        if(StringUtil.isEmpty(time)) {
            return null;
        }

        int year=0, month=1, day=1, hour=0, min=0, sec=0;

        List<String> strList = new ArrayList<String>();
        StringBuffer nsb = new StringBuffer();
        for(int i=0; i<time.length(); i++) {
            char c = time.charAt(i);
            if(Character.isDigit(c)) {
                nsb.append(c);
            } else {
                if(nsb.length() > 0) {
                    strList.add(nsb.toString());
                    nsb = new StringBuffer();
                }
            }
        }
        if(nsb.length() > 0) {
            strList.add(nsb.toString());
        }

        for(int i=0; i<strList.size(); i++) {
            int n = CommonUtil.getIntValue(strList.get(i));
            switch (i) {
                case 0:
                    year = n;
                    break;
                case 1:
                    month = n;
                    break;
                case 2:
                    day = n;
                    break;
                case 3:
                    hour = n;
                    break;
                case 4:
                    min = n;
                    break;
                case 5:
                    sec = n;
                    break;
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append(StringUtil.formatString("%04d", year)).append("-")
                .append(StringUtil.formatString("%02d", month)).append("-")
                .append(StringUtil.formatString("%02d", day)).append(" ")
                .append(StringUtil.formatString("%02d", hour)).append(":")
                .append(StringUtil.formatString("%02d", min)).append(":")
                .append(StringUtil.formatString("%02d", sec));
        return parseStringToDate(sb.toString(), "yyyy-MM-dd HH:mm:ss");
    }

    public static long diffMonth(Date end){
        Date begin=new Date();
        if(begin!=null && end!=null){
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM");
            formatDate.format(begin);
            formatDate.format(end);
            long diff = (begin.getTime()-end.getTime())/1000/3600/24;


            return diff;
        }
        return 0;
    }

    public static long diffMonth(Date begin ,Date end){
        if(begin!=null && end!=null){

            Calendar beginCal =  Calendar.getInstance();
            beginCal.setTime(begin);

            Calendar endCal =  Calendar.getInstance();
            endCal.setTime(end);

            long month=(endCal.get(Calendar.YEAR)-beginCal.get(Calendar.YEAR))*12;
            return month+(endCal.get(Calendar.MONTH)-beginCal.get(Calendar.MONTH));

        }
        return 0;
    }

    public static void main(String[] args) throws ParseException {
        
        long time = 1469495306000l;
        Date date = new Date(time);
        System.out.println(DateTimeUtil.formatDate(date, PATTERN_DEFAULT));
        
    }

    public static Date getYear(String year){
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy");
        try {
            return formatDate.parse(year);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    /**
     * 取一天最大的时间
     * @param date
     * @return
     */
    public static Date ceiling(Date date){
        if(date==null){
            return null;
        }
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        String day = sm.format(date) +" 23:59:59";
        return parseStringToDate(day,"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 取一天最小的时间
     * @param date
     * @return
     */
    public static Date floor(Date date){
        if(date==null){
            return null;
        }
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        String day = sm.format(date) +" 00:00:00";
        return parseStringToDate(day,"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 取一年最大的日期
     * @param date
     * @return
     */
    public static Date maxDate(Date date){
        if(date==null){
            return null;
        }
        SimpleDateFormat sm = new SimpleDateFormat("yyyy");
        String day = sm.format(date) +"-12-31 00:00:00";
        return parseStringToDate(day,"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Date类型转换Timestamp
     * @param date
     * @return
     */
    public static Timestamp toTimestamp(Date date){
        if(date==null){
            return null;
        }
        return new Timestamp(date.getTime());
    }
    /**
     * 获取差值（相隔几天）的时间
     * @param date
     * @param daltaDays daltaDays>0 表示后面几天 daltaDays<0 表示前面几天
     * @return
     */
    public static Date delta(Date date,int daltaDays){
        if(date==null){
            return null;
        }
        return new Date(date.getTime() + (daltaDays * 86400 * 1000l));
    }
    
    /**
     * Date类型转换sql Date
     * @param date
     * @return
     */
    public static java.sql.Date toSqlDate(Date date){
    	if(date==null){
            return null;
        }
    	return new java.sql.Date(date.getTime());
    }
    
    /**
     * 获取本月第一天
     */
    public static Date firstDayInMonth(){
    	Calendar cal = Calendar.getInstance();
    	// get start of the month
    	cal.set(Calendar.DAY_OF_MONTH, 1);
    	
    	return cal.getTime();
    }
    
    /**
     * 获取本月最后一天
     * 
     */
    public static Date lastDayInMonth(){
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
    	
    	return cal.getTime();
    }
}

