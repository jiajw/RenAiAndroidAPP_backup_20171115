package com.yousails.chrenai.framework.util;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jiajinwu
 * Date: 2017-11-01
 * Time: 16:58
 * 修改备注：
 * version:
 */


public class GsonUtils {

    private static Gson mGson;

    static {

        mGson = new Gson();
    }


    /***
     * Json对象解析
     * 将JSON String 字符串转换为传入类型的实体对象
     * @return
     */
    public static <T> T toBean(String jsonStr, Class<T> classOfT) {
        return new Gson().fromJson(jsonStr, classOfT);
    }


    /**
     * 转成list
     * 解决泛型问题
     * 把json 字符串转化成list集合
     *
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(String json, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(mGson.fromJson(elem, cls));
        }
        return list;
    }

    /**
     * 将对象或者list准换为json字符串
     *
     * @param object
     * @param <T>
     * @return
     */
    public static <T> String objectToString(T object) {
        return mGson.toJson(object);
    }
}
