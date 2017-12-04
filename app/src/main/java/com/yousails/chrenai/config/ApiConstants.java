package com.yousails.chrenai.config;

/**
 * 接口配置文件
 * Created by Administrator on 2017/6/15.
 */

public class ApiConstants {

    //是否是测试环境
    public static final boolean IS_TEST = false;

    //测试环境地址
    public static final String TEST_URL = "http://chrenai.yousails-project.com";
    //正式环境地址
    public static final String PRODUCT_URL = "https://app.chrenai.com";

    public static final String BASE_URL = IS_TEST ? TEST_URL : PRODUCT_URL;


    //  ------------------------------------登录---------------------------
    // 获取图片验证码
    public static final String GET_CAPTCHAS_API = BASE_URL + "/api/captchas";

    //发送短信验证码
    public static final String GET_VERIFICATION_CODES_API = BASE_URL + "/api/verification_codes";

    //验证短信验证码
    public static final String GET_REGISTER_KEYS = BASE_URL + "/api/register_keys";

    //注册
    public static final String GET_USERS_API = BASE_URL + "/api/users";

    //登录 (获取jwt) （json-web-token)
    public static final String GET_AUTHORIZATIONS_API = BASE_URL + "/api/authorizations";

    //刷新jwt---->PULL请求
    public static final String REFRESH_AUTHORIZATIONS_API = BASE_URL + "/api/authorizations/current";

    //第三方登录
    public static final String LOGIN_OAUTH_API = BASE_URL + "/api/oauth/authorizations";

    //清除注册用户
    public static final String CLEAN_USERS_API = BASE_URL + "/cleanUsers";

    //分类(从属)列表
    public static final String GET_CATEGORIES_API = BASE_URL + "/api/categories";

    //轮播图片
    public static final String GET_CAROUSELS_API = BASE_URL + "/api/carousels";

    //城市列表
    public static final String GET_CITIES_API = BASE_URL + "/api/cities";

    //根据坐标获取城市
    public static final String GET_CITY_API = BASE_URL + "/api/city";

    //活动列表
    public static final String GET_ACTIVITIES_API = BASE_URL + "/api/activities";

    //关键词
    public static final String GET_KEYWORDS_API = BASE_URL + "/api/keywords";

    //评论Api
    public static final String GET_COMMENTS_API = BASE_URL + "/api/activities/";

    //图片上传接口
    public static final String POST_FILE_UPLOAD = BASE_URL + "/api/assets";

    //活动发布
    public static final String POST_PUBLISH_API = BASE_URL + "/api/activities";

    //活动预览
    public static final String POST_PREVIEW_API = BASE_URL + "/api/activities/preview";


    //提交实名认证 (PULL)
    public static final String SUBMIT_CERTIFICATION_API = BASE_URL + "/api/user/authentication";

    /**
     * SharedPreference的key
     */
    public interface SharedKey {

        //本地存放的分类(从属)列表
        public static String CATEGORY_KEY = "category_key";

        //缓存活动发布信息
        public static String CACHED_ACTIVE_KEY = "cached_active_key";

        public static String KEY_BOARD_HEIGHT = "keyBoardHeight";
    }

    //点赞某个评论(put)
    public static final String SUPPORT_COMMENT_BY_ID = BASE_URL + "/api/user/voted/comments/";

    //取消点赞某个评论(DEL)
    public static final String DEL_SUPPORT_COMMENT_BY_ID = BASE_URL + "/api/user/voted/comments/";


    //学历数据
    public static final String GET_DEGREES_API = BASE_URL + "/api/degrees";

    //宗教数据
    public static final String GET_RELIGIONS_API = BASE_URL + "/api/religions";

    //我的发布
    public static final String USER_ACTIVITIES = BASE_URL + "/api/user/activities";

    public static final String USER_ENJOY_ACTIVITIES = BASE_URL + "/api/user/applied/activities";

    //签到
    public static final String USER_SIGN = BASE_URL + "/api/activities/";

    //获取用户信息
    public static final String GET_USER = BASE_URL + "/api/users/";

    //足迹
    public static final String GET_FOOTPRINT_API = BASE_URL + "/footprints?token=";

    //意见反馈
    public static final String POST_FEEDBACK_API = BASE_URL + "/api/system_feedbacks";

    //修改个人信息
    public static final String UPDATE_USERINFOR_API = BASE_URL + "/api/user";

    //检查身份证是否有效
    public static final String CHECK_IDENTITY_API = BASE_URL + "/api/identity_cards/";

    //活动分享
    public static final String POST_SHARED_API = BASE_URL + "/api/user/shared/ativities/";

    //环信用户列表
    public static final String EM_USERS_API = BASE_URL + "/api/easemob/users";

    public static final String BASE_ACTIVITY_API = BASE_URL + "/api/activities/";

    //获取活动权限列表
    public static final String GET_AUTHORITY_API = BASE_URL + "/api/activities/perms";

    //我的发布
    public static final String USER_ACTIVITIES_MANAGED = BASE_URL + "/api/user/managed/activities";

    //服务协议
    public static final String SERVICE_PROTOCOLS_URL = BASE_URL + "/protocols/volunteer";
    //活动协议
    public static final String PROTOCOLS_URL = BASE_URL + "/protocols/activity";

    //checkUpdate
    public static final String CHECK_VERSION_URL = BASE_URL + "/api/versions/latest";
    public static final String code_url = BASE_URL + "/user/qrcode?token=";

}
