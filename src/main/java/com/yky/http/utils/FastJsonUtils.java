package com.yky.http.utils;

import com.alibaba.fastjson.JSONObject;


public class FastJsonUtils {

    /**
     * 将任意java对象序列化成json
     * @param entity 要序列化的对象
     * @return jsonString
     */
    public static <T> String entityToJsonString(T entity) {
        if(entity == null) {
            return "";
        }

        //beautiful JSON
        String jsonString = JSONObject.toJSONString(entity,true);

        return jsonString;
    }

    /**
     * json字符串转换为任意java对象
     * 支持转换成集合
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jsonStringToEntity(String jsonString, Class<T> clazz) {
        T entity = JSONObject.parseObject(jsonString, clazz);

        return entity;
    }
}
