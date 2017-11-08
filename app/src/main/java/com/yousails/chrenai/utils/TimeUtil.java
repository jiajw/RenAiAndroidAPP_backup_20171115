/*
 *
 *@作者 nieshuting
 *@创建日期 2011-6-22下午06:00:04
 *@所有人 CDEL
 *@文件名 TimeUtil.java
 *@包名 org.cdel.chinaacc.phone.util
 */

package com.yousails.chrenai.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TimeUtil类实现时间与字符的转换；
 *
 * @author nieshuting
 * @version 0.1
 */
public class TimeUtil {

    /**
     * 转换秒数成时间字符串
     *
     * @param second 秒数 1000
     * @return 如'20:12'
     */
    public static String getString(int second) {
        StringBuffer str = new StringBuffer();
        StringBuffer hstring = new StringBuffer();
        StringBuffer mstring = new StringBuffer();
        StringBuffer sstring = new StringBuffer();
        int hour = 0;
        int minute = 0;
        if (second >= 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        if (hour < 10) {
            hstring.append("0").append(String.valueOf(hour));
        } else {
            hstring.append(String.valueOf(hour));
        }
        if (minute < 10) {
            mstring.append("0").append(String.valueOf(minute));
        } else {
            mstring.append(String.valueOf(minute));
        }
        if (second < 10) {
            sstring.append("0").append(String.valueOf(second));
        } else {
            sstring.append(String.valueOf(second));
        }
        if ("00".equals(hstring.toString())) {
            str.append(mstring).append(":").append(sstring);
        } else {
            str.append(hstring).append(":").append(mstring).append(":")
                    .append(sstring);
        }
        return str.toString();
    }

    /**
     * 转换时间字符串成秒数
     *
     * @param time 如'20:10:12'
     * @return second 秒
     */
    public static int getSecond(String time) {
        String[] times = time.split(":");
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (times.length == 2) {
            minute = Integer.parseInt(times[0]);
            second = Integer.parseInt(times[1]);
        } else {
            hour = Integer.parseInt(times[0]);
            minute = Integer.parseInt(times[1]);
            second = Integer.parseInt(times[2]);
        }
        return hour * 3600 + minute * 60 + second;
    }

    /**
     * 转换秒数成分钟时间字符串
     *
     * @param second 秒数 1000
     * @return 如'80'
     */
    public static int getMinute(int second) {
        int hour = 0;
        int minute = 0;
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        } else {
            return 1;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        return hour * 60 + minute;
    }

    /**
     * 转换秒数成小时,分钟时间字符串
     *
     * @param second 秒数 1000
     * @return 如'80'
     */
    public static String getHour(int second) {
        int hour = 0;
        int minute = 0;
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        } else {
            return "0分钟";
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
            return hour + "小时";
        } else {
            return minute + "分钟";
        }
    }


    /**
     * 转换分钟成小时,分钟时间字符串
     *
     * @param tempMinute 秒数 1000
     * @return 如'80'
     */
    public static String getTimeByMinute(int tempMinute) {
        int hour = 0;
        int minute = 0;
        if(0==tempMinute){
            return "";
        }

        if (tempMinute > 60) {
            hour = tempMinute / 60;
            minute = tempMinute % 60;
            return hour + "小时"+minute + "分钟";
        } else {
            return minute + "分钟";
        }
    }


    /**
     * 转换秒数成秒，分，小时字符串
     */
    public static String getTime(int second) {
        int minute = 0;
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
            return minute + "分" + second + "秒";
        } else {
            return second + "秒";
        }
    }

    /**
     * 将秒转换为几分几秒
     *
     * @param second
     * @return
     */
    public static String formatSecond(int second) {
        int minutes = (second % (60 * 60)) / 60;
        int seconds = second % (60);
        return minutes + " '" + seconds + " '' ";
    }


    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @param sdate
     * @return
     */
    public static String getWeek(String sdate) {
        // 再转换为时间
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将yyyy-MM-dd 转换为 yyyy .MM .dd
     *
     * @param strDate
     * @return
     */
    public static String parseDate(String strDate) {
        String tempDate = "";
        if (StringUtil.isNotNull(strDate)) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
                tempDate = new SimpleDateFormat("yyyy.MM.dd").format(date);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return tempDate;
        }
        return tempDate;
    }


    /**
     * 将yyyy-MM-dd HH:mm:ss转换为 上午 HH:mm:ss 开始
     *
     * @param strDate
     * @return
     */
    public static String parseTime(String strDate) {
        String mTime = "";
        if (StringUtil.isNotNull(strDate)) {
            try {
                //2017-06-29 16:14:55
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
                String tempTime = new SimpleDateFormat("HH:mm").format(date);
                String _12Time = new SimpleDateFormat("hh:mm").format(date);
                String[] timeStr = tempTime.split(":");

                if (timeStr != null && timeStr.length > 0) {
                    int time = Integer.parseInt(timeStr[0]);

                    if (time < 13) {
                        mTime = "上午  " + tempTime + "  开始";
                    }else {
                        mTime = "下午  " + _12Time + "  开始";
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return mTime;
        }
        return mTime;
    }

    //2017-07-18 14:36:31
    /**
     * 计算两个时间差
     */
    public static String getDistanceTime(String createTime){
        if(StringUtil.isEmpty(createTime)){
            return null;
        }
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long between = 0;
        try {
//            Date begin = dfs.parse("2017-07-18 14:36:31");
//            Date end = dfs.parse("2009-07-20 11:24:49.145");

//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//            System.out.println(df.format(new Date()));// new Date()为获取当前系统时间

            Date begin = dfs.parse(createTime);

           long nowTime= new Date().getTime();
           long starTime=begin.getTime();
            if(nowTime>starTime){
                between = (nowTime - starTime);// 得到两者的毫秒数
            }else{
                return between+"";
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
//        long ms = (between - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
//                - min * 60 * 1000 - s * 1000);
        if(day>0){
            if(1==day){
                return "昨天";
            }else if(2==day){
                return "前天";
            }else{

                String[] dayArray= createTime.toString().trim().split(" ");

                return dayArray[0];
            }

        }else if(hour>0){
            return hour + "小时前";
        }else if(min>0){
            return min + "分钟前";
        }else{
            return  s + "秒前";
        }

    }


    /**
     * 将指定日期转化为秒数
     */
    public static long getSecondsFromDate(String expireDate){
        if(expireDate==null||expireDate.trim().equals(""))
            return 0;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=null;
        try{
            date=sdf.parse(expireDate);
            return  date.getTime();
        }
        catch(ParseException e)
        {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 将yyyy-MM-dd HH:mm:ss转换为 yyyy-MM-dd HH:mm
     *
     * @param strDate
     * @return
     */
    public static String parseTime2(String strDate) {
        if (StringUtil.isNotNull(strDate)) {
            try {
                //2017-06-29 16:14:55
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
                String tempTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
                return tempTime;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return "";
    }

}
