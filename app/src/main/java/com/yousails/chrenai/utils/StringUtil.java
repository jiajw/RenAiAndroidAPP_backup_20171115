package com.yousails.chrenai.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/6/19.
 */

public class StringUtil {

    /**
     * 判断字符串是否为空
     *
     * @return true：不为空， false：为空
     */
    public static boolean isNotNull(String str) {
        if (str != null && !"".equals(str)) {
            return true;
        }
        return false;
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean 若输入字符串为null或空字符串，返回true
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 转换输入流为字符串
     *
     * @param is 指定要转换的输入流
     * @return String 字符串
     */
    public static String getString(InputStream is) {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return buffer.toString();
    }

    /**
     * 检查是否是邮箱
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        // Pattern emailer = Pattern
        // .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Pattern emailer = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        return emailer.matcher(email).matches();
    }

    /**
     * 判断手机号码是否正确
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        phoneNumber = trimmy(phoneNumber);
        PhoneUtil mobile = new PhoneUtil(phoneNumber);
        return mobile.isLawful();
    }

    public static String trimmy(String str) {
        String dest = "";
        if (str != null) {
            str = str.replaceAll("-", "");
            str = str.replaceAll("\\+", "");
            dest = str;
        }
        return dest;
    }

    /**
     * 校验用户名是否合法，规则4-20个字符（可以为字母、数字或下划线'_'，不能包含空格）
     *
     * @param name 用户名
     * @return true为合法
     */
    public static boolean checkName(String name) {
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z_]{4,20}$");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    /**
     * 校验用户姓名是否为汉字，规则2-5个汉字
     *
     * @param name 要校验的姓名
     * @return true为合法
     */
    public static boolean checkNameIsChinese(String name) {
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]{2,5}");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }



    public static String stringFilter(String str){
        String regEx = "[/\\:*?<>|\"\n\t]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }


    /**
     * 格式化返回km
     *
     * @param dis
     */
    public static String formatDistance(String dis){
        int index;
        if(isNotNull(dis)){
            String temp=String.valueOf(Double.valueOf(dis));
            index=temp.indexOf(".");

            int tempDis=Integer.parseInt(temp.substring(0,index));

            if(tempDis<1000){
                if(0==tempDis)return "";
                return  tempDis+"m";
            }else{
                temp=String.valueOf(Double.valueOf(dis)/1000);
                index=temp.indexOf(".");
                String meter=temp.substring(index+1,index+2);
                if(!"0".equals(meter)){
                    return temp.substring(0,index+2)+"km";
                }
                return temp.substring(0,index)+"km";
            }
        }
        return "";
    }

}
