
package com.lanou.lilyxiao.myapplication.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON处理工具类。
 */
public class JsonUtil {

    /**
     * 从一个JSONObject中读取一个字符串。
     * 
     * @param o JSONObject
     * @param name 字符串字段名
     * @return 字符串，如果失败返回空串
     */
    public static String getString(JSONObject o, String name) {
        if (o.has(name)) {
            try {
                return o.getString(name);
            } catch (JSONException e) {
            }
        }
        return "";
    }

    /**
     * 从一个JSONObject中读取一个int。
     * 
     * @param o JSONObject
     * @param name 字段名
     * @return 值，如果失败返回0
     */
    public static int getInt(JSONObject o, String name) {
        if (o.has(name)) {
            try {
                return o.getInt(name);
            } catch (JSONException e) {
            }
        }
        return 0;
    }

    /**
     * 从一个JSONObject中读取一个double。
     * 
     * @param o JSONObject
     * @param name 字段名
     * @return 值，如果失败返回0
     */
    public static double getDouble(JSONObject o, String name) {
        if (o.has(name)) {
            try {
                return o.getDouble(name);
            } catch (JSONException e) {
            }
        }
        return 0;
    }

    /**
     * 从一个JSONObject中读取一个子JSONObject。
     * 
     * @param o JSONObject
     * @param name 字段名
     * @return JSONObject值，如果失败返回null
     */
    public static JSONObject getJSONObject(JSONObject o, String name) {
        if (o.has(name)) {
            try {
                return o.getJSONObject(name);
            } catch (JSONException e) {
            }
        }
        return null;
    }

    /**
     * 从一个JSONObject中读取一个子Object。
     * 
     * @param o JSONObject
     * @param name 字段名
     * @return Object值，如果失败返回null
     */
    public static Object getObject(JSONObject o, String name) {
        if (o.has(name)) {
            try {
                return o.get(name);
            } catch (JSONException e) {
            }
        }
        return null;
    }

    /**
     * 从一个JSONObject中读取一个子JSONArray。
     * 
     * @param o JSONObject
     * @param name 字段名
     * @return JSONArray值，如果失败返回null
     */
    public static JSONArray getJSONArray(JSONObject o, String name) {
        if (o.has(name)) {
            try {
                return o.getJSONArray(name);
            } catch (JSONException e) {
            }
        }
        return null;
    }

    /**
     * 去掉字符串开头的“varxxx”，仅保留纯JSON部分。这种结果可能是误传的来自web版结果。
     * 
     * @param dataStr 原始字符串
     * @return 留纯JSON部分
     */
    public static String filterWebString(String dataStr) {
        if (dataStr.startsWith("var")) {
            int idx = dataStr.indexOf("{");
            return dataStr.substring(idx);
        }
        return dataStr;
    }
}
