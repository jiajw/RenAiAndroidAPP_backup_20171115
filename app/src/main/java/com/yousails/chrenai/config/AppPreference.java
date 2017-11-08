package com.yousails.chrenai.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/6/15.
 */

public class AppPreference {
    private static AppPreference preference;
    private SharedPreferences mSP;

    // 是否登录；
    private static final String IS_LOGIN = "is_login";
    // 是否注销；
    private static final String IS_LOGOUT = "is_logout";

    private static final String USERID = "uid";
    //最后登录用户id
    private static final String LASTUSERID = "last_uid";
    private static final String NAME = "name";
    private static final String REAL_NAME ="realname";
    private static final String PHONE = "phone";

    //环信用户信息
    private static final String EM_NAME = "em_name";
    private static final String EM_PWD = "en_pwd";

    //性别
    private static final String GENDER = "gender";
    //用户头像
    private static final String AVATAR = "avatar";
    //是否实名认证
    private static final String ISCERTIFICATED = "is_certificated";
    //审核状态
    private static final String STATUS="user_status";
    //是否是vip
    private static final String ISVIP = "is_vip";
    //宗教信仰
    private static final String RELIGION="religion";
    //用户星级
    private static final String USER_LEVEL="user_level";

    //是否被加入黑名单
    private static final String ISBANNED = "is_banned";
    //参加活动总时长
    private static final String WORKING_HOURS = "working_hours";

    //是否是初次定位城市
    private static final String FIRST_LOCAL_CITY="firstlocalcity";

    //验证短信验证码
    private static final String REGISTER_KEY = "register_keys";

    //主页活动分类
    private static final String CATEGORY_ID="category_id";
    //keywords
    private static final String KEYWORDS="keywords";

    //当前城市
    private static final String CURRENT_CITY="current_city";
    //定位城市
    private static final String LOCATION_CITY="location_city";

    //距离筛选
    private static final String FILTER_DISTANCE="filter_distance";
    //时间筛选
    private static final String FILTER_DAY="filter_day";

    /**
     * 令牌码TOKEN
     */
    private static final String TOKEN = "token_new";
    /**
     * 令牌码过期时间
     */
    private static final String EXPIRED_AT = "expired_at";
    /**
     * 令牌码刷新时间
     */
    private static final String REFRESH_EXPIRED_AT = "refresh_expired_at";


    /**
     * 经纬度
     */
    private static final String LONGITUDE="longitude";
    private static final String LATITUDE="latitude";


    //--------------------------------------微信----------------------------------------
    private static final String WEIXIN_ACCESS_TOKEN="weixin_access_token";
    private static final String WEIXIN_REFRESH_TOKEN="weixin_refresh_token";
    private static final String WEIXIN_OPENID="weixin_openid";


    private AppPreference(Context context) {
        mSP = context.getSharedPreferences("chrenai", Context.MODE_PRIVATE);
    }

    public static AppPreference getInstance(Context context) {
        if (preference == null) {
            preference = new AppPreference(context);
        }
        return preference;
    }

    /**
     * userId
     */
    public String readUerId() {
        return mSP.getString(USERID, "");
    }

    public void writeUserId(String userId) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(USERID, userId);
        editor.commit();
    }

    /**
     * last userId
     */
    public String readLastUerId() {
        return mSP.getString(LASTUSERID, "");
    }

    public void writeLastUserId(String userId) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(LASTUSERID, userId);
        editor.commit();
    }

    /**
     * name
     */
    public String readUserName() {
        return mSP.getString(NAME, "");
    }

    public void writeUserName(String name) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(NAME, name);
        editor.commit();
    }

    /**
     * realname
     */
    public String readRealName() {
        return mSP.getString(REAL_NAME, "");
    }

    public void writeRealName(String name) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(REAL_NAME, name);
        editor.commit();
    }


    /**
     * phone
     */
    public void writePhone(String uid,String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(uid+PHONE, value);
        editor.commit();
    }

    public String readPhone(String uid) {
        return mSP.getString(uid+PHONE, "");
    }

    /**
     * AVATAR
     */
    public void writeAvatar(String avatar) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(AVATAR, avatar);
        editor.commit();
    }

    public String readAvatar() {
        return mSP.getString(AVATAR, "");
    }

    /**
     * GENDER
     */
    public void writeGender(String gender) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(GENDER, gender);
        editor.commit();
    }

    public String readGender() {
        return mSP.getString(GENDER, "male");
    }

    /**
     * isVip
     */
    public void writeIsVip(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(ISVIP, value);
        editor.commit();
    }

    public String readIsVip() {
        return mSP.getString(ISVIP, "");
    }


    /**
     * 实名认证 certification;
     */
    public void writeCertification(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(ISCERTIFICATED, value);
        editor.commit();
    }

    public String readCertification() {
        return mSP.getString(ISCERTIFICATED, "");
    }


    /**
     * 实名认证审核状态;
     */
    public void writeStatus(String uid,String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(uid+STATUS, value);
        editor.commit();
    }

    public String readStatus(String uid) {
        return mSP.getString(uid+STATUS, "");
    }






    /**
     * 宗教信仰
     */
    public void writeReligion(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(RELIGION, value);
        editor.commit();
    }

    public String readReligion() {
        return mSP.getString(RELIGION, "");
    }



    /**
     * TOKEN
     */
    public void writeToken(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(TOKEN, value);
        editor.commit();
    }

    public String readToken() {
        return mSP.getString(TOKEN, "");
    }


    /**
     * 令牌码过期时间
     */
    public void writeExpired(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(EXPIRED_AT, value);
        editor.commit();
    }

    public String readExpired() {
        return mSP.getString(EXPIRED_AT, "");
    }


    /**
     * 令牌码刷新时间
     */
    public void writeRefreshExpired(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(REFRESH_EXPIRED_AT, value);
        editor.commit();
    }

    public String readRefreshExpired() {
        return mSP.getString(REFRESH_EXPIRED_AT, "");
    }


    /**
     * 验证短信验证码
     */
    public void writeRegKey(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(REGISTER_KEY, value);
        editor.commit();
    }

    public String readRegKey() {
        return mSP.getString(REGISTER_KEY, "");
    }

    /**
     * isLogin
     */
    public void setLogin(boolean value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putBoolean(IS_LOGIN, value);
        editor.commit();
    }

    public boolean readLogin() {
        return mSP.getBoolean(IS_LOGIN, false);
    }

    /**
     * isLogout
     */
    public void setLogout(boolean value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putBoolean(IS_LOGOUT, value);
        editor.commit();
    }

    public boolean readLogout() {
        return mSP.getBoolean(IS_LOGOUT, false);
    }


    /**
     * CategoryId
     */
    public void writeCategoryId(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(CATEGORY_ID, value);
        editor.commit();
    }

    public String readCategoryId() {
        return mSP.getString(CATEGORY_ID, "");
    }

    /**
     * keyword
     */
    public void writeKeyWords(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(KEYWORDS, value);
        editor.commit();
    }

    public String readKeyWords() {
        return mSP.getString(KEYWORDS, "");
    }


    /**
     * 当前城市
     */
    public void writeCurrentCity(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(CURRENT_CITY, value);
        editor.commit();
    }

    public String readCurrentCity() {
        return mSP.getString(CURRENT_CITY, "1,北京");
    }

    /**
     * 定位城市
     */
    public void writeLocationCity(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(LOCATION_CITY, value);
        editor.commit();
    }

    public String readLocationCity() {
        return mSP.getString(LOCATION_CITY, "1,北京");
    }

    /**
     * 距离筛选
     */
    public void writefilterDistance(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(FILTER_DISTANCE, value);
        editor.commit();
    }

    public String readFilterDistance() {
        return mSP.getString(FILTER_DISTANCE, "");
    }

    /**
     * 时间筛选
     */
    public void writefilterDay(String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(FILTER_DAY, value);
        editor.commit();
    }

    public String readFilterDay() {
        return mSP.getString(FILTER_DAY, "");
    }


    /**
     * 微信token
     */
    public void writeWXToken(String token){
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(WEIXIN_ACCESS_TOKEN, token);
        editor.commit();
    }

    public String readWXToken(){
        return mSP.getString(WEIXIN_ACCESS_TOKEN, "");
    }

    /**
     * 微信refresh token
     */
    public void writeWXRefreshToken(String token){
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(WEIXIN_REFRESH_TOKEN, token);
        editor.commit();
    }

    public String readWXRefreshToken(){
        return mSP.getString(WEIXIN_REFRESH_TOKEN, "");
    }

    /**
     * 微信openid
     */
    public void writeWXOpenId(String openId){
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(WEIXIN_OPENID, openId);
        editor.commit();
    }

    public String readWXOpenId(){
        return mSP.getString(WEIXIN_OPENID, "");
    }

    /**
     * 经度
     */
    public void writeLongitude(String longitude){
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(LONGITUDE, longitude);
        editor.commit();
    }

    public String readLongitude(){
        return mSP.getString(LONGITUDE, "116.54135239892");
    }

    /**
     * 纬度
     */
    public void writeLatitude(String latitude){
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(LATITUDE, latitude);
        editor.commit();
    }

    public String readLatitude(){
        return mSP.getString(LATITUDE, "40.048014519523");
    }


    /**
     * 是否是初次定位城市
     */
    public void writeFirLocalCity(String value){
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(FIRST_LOCAL_CITY, value);
        editor.commit();
    }

    public String readFirLocalCity(){
        return mSP.getString(FIRST_LOCAL_CITY, "0");
    }



    /**
     * 环信用户名
     */
    public String readEMName() {
        return mSP.getString(EM_NAME, "");
    }

    public void writeEMName(String name) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(EM_NAME, name);
        editor.commit();
    }


    /**
     * 环信用户密码
     */
    public String readEMPwd() {
        return mSP.getString(EM_PWD, "");
    }

    public void writeEMPwd(String name) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(EM_PWD, name);
        editor.commit();
    }


    /**
     * 用户星级
     */
    public String readLevel() {
        return mSP.getString(USER_LEVEL, "");
    }

    public void writeLevel(String name) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(USER_LEVEL, name);
        editor.commit();
    }


    /**
     * 工作时长
     */
    public String readWorkHours() {
        return mSP.getString(WORKING_HOURS, "");
    }

    public void writeWorkHours(String name) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(WORKING_HOURS, name);
        editor.commit();
    }


}
