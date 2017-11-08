package com.yousails.chrenai.login.listener;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.yousails.chrenai.app.ui.MainActivity;
import com.yousails.chrenai.login.ui.LoginActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/6/21.
 */

public class SmsObserver extends ContentObserver {
    private Uri SMS_INBOX = Uri.parse("content://sms/inbox");
    private Context mContext;
    private Handler mHandler;
    private String code; // 验证码

    public SmsObserver(Context context, Handler handler) {
        super(handler);
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        // 接收到短信时，onChange会调用两次。第一次还没有将信息写入数据库中。所以我们不处理
      /*  if (uri.toString().equals("content://sms/raw")) {
            return;
        }

        String code = "";

        Uri inboxUri = Uri.parse("content://sms/inbox");

        // 按短信ID倒序排序，避免修改手机时间数据不正确
        //Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)

        String[] projection = new String[] { "body" };
        String where = " body like '%仁爱慈善%' AND date >" + (System.currentTimeMillis() - 10 * 60 * 1000);

        Cursor c = mContext.getContentResolver().query(inboxUri, projection, where,
                null, "_id desc");

        if (c != null) {
            if (c.moveToFirst()) {
                String address = c.getString(c.getColumnIndex("address"));// 发送人号码
                String body = c.getString(c.getColumnIndex("body")); // 短信内容


                // 正则表达式。表示连续6位的数字,可以在这边修改成自己所要的格式
                Pattern pattern = Pattern.compile("(\\d{6})");
                // 匹配短信内容
                Matcher matcher = pattern.matcher(body);
                if (matcher.find()) {
                    code = matcher.group(0);
                    mHandler.obtainMessage(LoginActivity.MSG_RECEIVED_CODE, code)
                            .sendToTarget();
                }
            }
        }*/

        //每当有新短信到来时，使用我们获取短消息的方法
//         getSmsFromPhone();




// 第一次回调 不是我们想要的 直接返回
       /* if (uri.toString().equals("content://sms/raw")) {
            return;
        }*/
        // 第二次回调 查询收件箱里的内容
        Uri inboxUri = Uri.parse("content://sms/inbox");
// 按时间顺序排序短信数据库
        Cursor c = mContext.getContentResolver().query(inboxUri, null, null, null, "date desc");
        if (c != null) {
            if (c.moveToFirst()) {
                // 获取手机号
                String address = c.getString(c.getColumnIndex("address"));
                // 获取短信内容
                String body = c.getString(c.getColumnIndex("body"));
                // 判断手机号是否为目标号码，服务号号码不固定请用正则表达式判断前几位。
                if (!address.equals("+86目标手机号，这里一定要有'+86'这个字段")) {
                    return;
                }
// 正则表达式截取短信中的6位验证码
                Pattern pattern = Pattern.compile("(\\d{6})");
                Matcher matcher = pattern.matcher(body);
// 如果找到通过Handler发送给主线程
                if (matcher.find()) {
                    code = matcher.group(0);
                    mHandler.obtainMessage(1, code).sendToTarget();
                }
            }
        }
        c.close();


    }



    public void getSmsFromPhone() {
        ContentResolver cr = mContext.getContentResolver();
        String[] projection = new String[] { "body","address" };//"_id", "address", "person",, "date", "type
        String where = " date >  "
                + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToFirst()) {
            String number = cur.getString(cur.getColumnIndex("address"));//发送人号码
            String body = cur.getString(cur.getColumnIndex("body"));// 短信内容
            //TODO 这里是具体处理逻辑
            // 正则表达式。表示连续6位的数字,可以在这边修改成自己所要的格式
            Pattern pattern = Pattern.compile("(\\d{6})");
            // 匹配短信内容
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
              String  code = matcher.group(0);
                mHandler.obtainMessage(LoginActivity.MSG_RECEIVED_CODE, code)
                        .sendToTarget();
            }
        }
    }


}
