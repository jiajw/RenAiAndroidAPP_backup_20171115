package com.yousails.chrenai.login.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/7/23.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {
    private static MessageListener mMessageListener;
    public SMSBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Object [] pdus= (Object[]) intent.getExtras().get("pdus");
        for(Object pdu:pdus){
            SmsMessage smsMessage=SmsMessage.createFromPdu((byte [])pdu);
            String sender=smsMessage.getDisplayOriginatingAddress();
            String content=smsMessage.getMessageBody();
//            long date=smsMessage.getTimestampMillis();
//            Date timeDate=new Date(date);
//            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String time=simpleDateFormat.format(timeDate);

            System.out.println("短信来自:"+sender);
            System.out.println("短信内容:"+content);
//            System.out.println("短信时间:"+time);
            String smsCode=getDynamicPassword(content);
            mMessageListener.OnReceived(smsCode);

            //如果短信来自5556,不再往下传递,一般此号码可以作为短信平台的号码。
            if("5556".equals(sender)){
                System.out.println(" abort ");
                abortBroadcast();
            }

        }
    }


    /**
     * 从字符串中截取连续6位数字组合 ([0-9]{" + 6 + "})截取六位数字 进行前后断言不能出现数字 用于从短信中获取动态密码
     *
     * @param str
     *            短信内容
     * @return 截取得到的6位动态密码
     */
    public String getDynamicPassword(String str) {
        // 6是验证码的位数一般为六位
        Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{"
                + 6 + "})(?![0-9])");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            System.out.print(m.group());
            dynamicPassword = m.group();
        }

        return dynamicPassword;
    }

    // 回调接口
    public interface MessageListener {
        public void OnReceived(String message);
    }

    public void setOnReceivedMessageListener(MessageListener messageListener) {
        this.mMessageListener=messageListener;
    }
}
