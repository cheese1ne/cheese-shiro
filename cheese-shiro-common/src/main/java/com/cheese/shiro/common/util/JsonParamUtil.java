package com.cheese.shiro.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * json参数工具
 *
 * @author sobann
 */
public class JsonParamUtil {

    private final static Pattern JsonKeysPattern = Pattern.compile("(?<=\\{)[^\\}]+");

    /**
     * 属性.属性来取json字符串中的数值
     *
     * @param json
     * @param keys link.name
     * @return
     */
    public static Set<String> getJsonVaue(String json, String keys) {
        Set<String> set = new HashSet<>();
        try {
            Object parse = JSON.parse(json);
            if ("".equals(keys)) {
                if (parse instanceof JSONArray) {
                    for (Object o : (JSONArray) parse) {
                        set.add(o.toString());
                    }
                } else if (!(parse instanceof JSONObject)) {
                    set.add(parse.toString());
                }
            } else {
                getJsonValue(parse, keys, set);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return set;
    }

    /**
     * 跟据 .propertity.propertity 递归解析json
     *
     * @param json
     * @param keys
     * @param values
     */
    private static void getJsonValue(Object json, String keys, Set<String> values) {
        String key = keys.split("\\.")[0];
        int index = keys.indexOf(".");
        if (json instanceof JSONObject) {
            json = ((JSONObject) json).get(key);
            if (index > 0) {
                String newkey = keys.substring(index + 1);
                getJsonValue(json, newkey, values);
            } else {
                if (json instanceof JSONArray) {
                    JSONArray array = (JSONArray) json;
                    for (Object o : array) {
                        values.add(o.toString());
                    }
                } else if (!(json instanceof JSONObject)) {
                    values.add(json.toString());
                }
            }
        } else if (json instanceof JSONArray) {
            JSONArray array = (JSONArray) json;
            for (Object o : array) {
                getJsonValue(o, keys, values);
            }
        }
    }

    /**
     * 获取{}中数值
     * {json.id} 获取 json.id
     * 如果直接为{},直接取json本身或者数据集合
     *
     * @param parameter
     * @return
     */
    public static String getJsonKey(String parameter) {
        if ("{}".equals(parameter)) {
            return "";
        }
        String keys = null;
        Matcher matcher = JsonKeysPattern.matcher(parameter);
        if (matcher.find()) {
            keys = matcher.group();
        }
        return keys;
    }

}
