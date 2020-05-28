package com.cgcg.context.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期工具类
 * @author xujinbang
 * @date 2019-8-14
 */
public class DateUtil extends tool.util.DateUtil{
	
	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
	

	public static String format(SimpleDateFormat dateFormat, Date date){
		return dateFormat.format(date);
	}

    @SuppressWarnings("deprecation")
	public static Date dateAddMins(Date date, int minCnt) {
        Date d = new Date(date.getTime());
        d.setMinutes(d.getMinutes() + minCnt);
        return d;
    }
    
    /**
     * 计算时间差,单位分
     * @param date1
     * @param date2
     * @return
     */
    public static int minuteBetween(Date date1, Date date2){
		DateFormat sdf=new SimpleDateFormat(DATEFORMAT_STR_001);
		Calendar cal = Calendar.getInstance();
		try {
			Date d1 = sdf.parse(DateUtil.dateStr4(date1));
			Date d2 = sdf.parse(DateUtil.dateStr4(date2));
			cal.setTime(d1);
			long time1 = cal.getTimeInMillis();
			cal.setTime(d2);
			long time2 = cal.getTimeInMillis();
			return Integer.parseInt(String.valueOf((time2 - time1) / 60000));
		} catch (ParseException e) {
			logger.error(e.getMessage(),e);
		}
		return 0;
	}

	/**
	 * 计算两个日期是否在day天以内(开始日期取当前天00:00:00)
	 * @param start 开始日期
	 * @param end   结束日期
	 * @param day   天数
	 * @return
	 */
	public static boolean dateSuperLong(Date start, Date end,int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);
		calendar.set(Calendar.HOUR_OF_DAY, 0);// 时
		calendar.set(Calendar.MINUTE, 0);// 分
		calendar.set(Calendar.SECOND, 0);// 秒
		calendar.set(Calendar.MILLISECOND, 0); // 毫秒
		long startLong = calendar.getTimeInMillis();
		long dateLong = 1000l *60l*60l*24l  * (long)day;
 		long value = end.getTime() - startLong - dateLong;
		return value < 0;
	}


	public static Date getLastSecIntegralTime(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(d.getTime());
		cal.set(11, 23);
		cal.set(13, 59);
		cal.set(12, 59);
		cal.set(14, 0);
		return cal.getTime();
	}
    
	/**
	 * 获取指定时间天的开始时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDayStartTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DATE), 0, 0, 0);
		return cal.getTime();
	}

	/**
	 * 获取指定时间天的结束时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDayEndTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DATE), 23, 59, 59);
		return cal.getTime();
	}

	/**
	 * String转化Date格式
	 * @param date
	 * @param type
	 * @return
	 */
	public static Date parse(String date,String type){
		SimpleDateFormat formatter = new SimpleDateFormat(type);
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(date, pos);
		return strtodate;
		
	}
	
	/**
	 * 得到指定日期之间的天数集合
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static List<Date> dateSplit(Date startDate, Date endDate)
	        throws Exception {
	    if (!startDate.before(endDate))
	        throw new Exception("开始时间应该在结束时间之后");
	    Long spi = endDate.getTime() - startDate.getTime();
	    Long step = spi / (24 * 60 * 60 * 1000);// 相隔天数

	    List<Date> dateList = new ArrayList<Date>();
	    dateList.add(endDate);
	    for (int i = 1; i <= step; i++) {
	        dateList.add(new Date(dateList.get(i - 1).getTime()
	                - (24 * 60 * 60 * 1000)));// 比上一天减一
	    }
	    return dateList;
	}
	
	/**
	 * 得到指定日期之间的月数集合
	 * @param minDate
	 * @param maxDate
	 * @return
	 * @throws ParseException
	 */
	public static List<String> getMonthBetween(String minDate, String maxDate) throws ParseException{
	    ArrayList<String> result = new ArrayList<String>();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

	    Calendar min = Calendar.getInstance();
	    Calendar max = Calendar.getInstance();

	    min.setTime(sdf.parse(minDate));
	    min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

	    max.setTime(sdf.parse(maxDate));
	    max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

	    Calendar curr = min;
	    while (curr.before(max)) {
	     result.add(sdf.format(curr.getTime()));
	     curr.add(Calendar.MONTH, 1);
	    }

	    return result;
	  }
	
	/**
	 * 得到指定之前的前后几天
	 * @param day
	 * @param date
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static Date getDateBefore(int day,Date date){
		Calendar   calendar   =   new GregorianCalendar(); 
		calendar.setTime(date); 
		calendar.add(calendar.DATE,day);//把日期往后增加一天.整数往后推,负数往前移动 
		date=calendar.getTime();
		return date;
	}

	@SuppressWarnings("deprecation")
	public static Date dateAddDays(Date date, int days) {
		Date d = new Date(date.getTime());
		d.setDate(d.getDate() + days);
		return d;
	}
	
	public static Date dateAddMonths(Date date, int months) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, months);
		return c.getTime();
	}
	
	
	/**
	 *  分钟转天、时、分
	 * @param minute
	 * @return
	 */
	public static String minuteToTimes(int minute){
		String DateTimes = null;
		long days = minute / ( 60 * 24);
		long hours = (minute % ( 60 * 24)) / 60;
		long minutes = minute % 60;
		if(days>0){
		   DateTimes= days + "天" + hours + "小时" + minutes + "分钟"; 
		}else if(hours>0){
		   DateTimes=hours + "小时" + minutes + "分钟"; 
		}else if(minutes>0){
		   DateTimes=minutes + "分钟"; 
		}
		
		return DateTimes;
	}
	/**
     * 格式化Date时间
     * @param time Date类型时间
     * @param timeFromat String类型格式
     * @return 格式化后的字符串
     */
    public static String parseDateToStr(Date time, String timeFromat){
    	DateFormat dateFormat=new SimpleDateFormat(timeFromat);
    	return dateFormat.format(time);
    }


	/**
	 * 判断是否是同一天
	 * @param day1
	 * @param day2
	 * @return
	 */
	public static boolean isSameDay(Date day1, Date day2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String ds1 = sdf.format(day1);
		String ds2 = sdf.format(day2);
		return ds1.equals(ds2);
	}

	/**
	 * 计算两个时间之间的小时数
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	public static long getHourBetween(Date firstDate,Date secondDate) {
		long l = firstDate.getTime() - secondDate.getTime();
		//算出来的是隔多少小时
		return l/(3600*1000);
	}

	/**
	 * 获取时分
	 * @return
	 */
	public static String getHour(Date date) {
		Calendar time = Calendar.getInstance();
		time.setTime(date);
		time.get(Calendar.HOUR_OF_DAY);//获取小时
		time.get(Calendar.MINUTE);//获取分钟
		return String.valueOf(time.get(Calendar.HOUR_OF_DAY)) + String.valueOf(time.get(Calendar.MINUTE));
	}


	/**
	 * 计算年龄
	 * @param birthDay
	 * @return
	 * @throws Exception
	 */
	public static int getAge(Date birthDay){
		Calendar cal = Calendar.getInstance();

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthDay);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth) age--;
			}else{
				age--;
			}
		}
		return age;
	}

	/**
	 * 计算2个时间的的毫秒数，day2>day1
	 * @param day1
	 * @param day2
	 * @return
	 */
	public static long getMillisecondBetween(Date day1, Date day2){
		return day2.getTime() - day1.getTime();
	}

	/**
	 * 获取 10位时间戳
	 * @return
	 */
	public static Integer getDateInt() {
		return (int) (System.currentTimeMillis() / 1000);
	}

}
