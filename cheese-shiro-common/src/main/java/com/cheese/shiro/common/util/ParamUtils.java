package com.cheese.shiro.common.util;

import com.cheese.shiro.common.Context;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 参数工具
 *
 * @author sobann
 */
public class ParamUtils {

    private static PathMatcher pathMatcher = new AntPathMatcher();

    private final static String cachePrefix = "paramCache_";


    /**
     * 从上下文环境中，替换表达式
     * $直接取值,{}json取值
     * $param:{param}:....
     *
     * @param valueExpress
     * @param context      须保证context完整
     * @return
     */
    public static String getValueWithExpress(String valueExpress, Context context) {
        return getValueWithExpress(valueExpress, context, context.getBestUriPattern(), context.getRequestUri());
    }

    /**
     * 从上下文环境中，替换表达式
     * $直接取值,{}json取值
     * $param:{param}:....
     *
     * @param valueExpress
     * @param context
     * @param UrlPattern
     * @param requestURI
     * @return
     */
    public static String getValueWithExpress(String valueExpress, Context context, String UrlPattern, String requestURI) {
        if (valueExpress == null) {
            return null;
        }
        //非动态标识符，直接返回
        if (!valueExpress.contains("$") && !valueExpress.contains("{") && !valueExpress.contains("}")) {
            return valueExpress;
        }
        //动态标识符，进行转换
        StringBuffer buffer = new StringBuffer();
        String[] split = valueExpress.split(":");
        for (int i = 0; i < split.length; i++) {
            String param = split[i];
            Set<String> result = null;
            if (param.startsWith("$")) {
                String original = param.substring(1);
                result = getParameterFromRequestAndBody(context, original, UrlPattern, requestURI);
            } else if (param.startsWith("{") && param.endsWith("}")) {
                result = getParameterFromRequestAndBody(context, param, UrlPattern, requestURI);
            }
            if (CollectionUtils.isEmpty(result)) {
                buffer.append(param);
            } else {
                //只取第一个
                buffer.append(result.iterator().next());
            }
            if (i != split.length - 1) {
                buffer.append(":");
            }
        }
        return buffer.toString();
    }

    public static Set<String> getParameterFromRequestAndBody(Context context, String name) {
        return getParameterFromRequestAndBody(context, name, context.getBestUriPattern(), context.getRequestUri());
    }

    /**
     * 从请求中获取参数
     * 先从context中获取
     *
     * @param context    上下文
     * @param name       参数名称
     * @param UrlPattern 请求uri格式（获取路径参数）
     * @param requestURI 请求uri(获取路径参数)
     * @return
     */
    public static Set<String> getParameterFromRequestAndBody(Context context, String name, String UrlPattern, String requestURI) {
        //查看context中是否已经存在
        String cacheKey = getCacheKey(name);
        Object cache = context.getAttribute(cacheKey);
        if (cache != null) {
            return (Set<String>) cache;
        }
        //检查是否从json中取值{json.id}，参数名符合{...}
        String jsonKey = JsonParamUtil.getJsonKey(name);
        Set<String> values = null;
        //从requestBody中的 json中取值
        if (jsonKey != null) {
            String contentType = context.getRequestContentType();
            //检查是否为json内容
            if (StringUtils.isBlank(contentType) || !contentType.contains("application/json")) {
                return null;
            }
            //获取requestBody
            String body = null;
            try {
                body = context.getRequestBody();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (StringUtils.isNotBlank(body)) {
                values = JsonParamUtil.getJsonVaue(body, jsonKey);
            }
        } else {
            //query/form/path 参数提取
            String value = getParamterFromRequest(context, name, UrlPattern, requestURI);
            if (value != null) {
                values = new HashSet<>();
                values.add(value);
            }

        }
        //放入context
        if (values != null) {
            context.setAttribute(cacheKey, values);
        }
        return values;
    }


    public static String getParamterFromRequest(Context context, String name) {
        return getParamterFromRequest(context, name, context.getBestUriPattern(), context.getRequestUri());
    }


    /**
     * 获取query/path/form参数
     *
     * @param context
     * @param name
     * @param urlPattern
     * @param requestURI
     * @return
     */
    public static String getParamterFromRequest(Context context, String name, String urlPattern, String requestURI) {
        String value = null;
        //先进行uripath提倡
        //查看是否存在uripath缓存
        Object zondyUriTemplateVariables = context.getAttribute("uriTemplateVariables");
        if (zondyUriTemplateVariables != null) {
            Map<String, String> map = null;
            try {
                map = (Map<String, String>) zondyUriTemplateVariables;
            } catch (Exception e) {

            }
            if (!CollectionUtils.isEmpty(map)) {
                value = map.get(name);
            }
        } else if (StringUtils.isNotBlank(urlPattern) && StringUtils.isNotBlank(requestURI)) {
            //进行提取，并进行缓存
            Map<String, String> map = pathMatcher.extractUriTemplateVariables(urlPattern, requestURI);
            if (!CollectionUtils.isEmpty(map)) {
                value = map.get(name);
                context.setAttribute("uriTemplateVariables", map);
            }
        }
        //进行query/form提参
        if (StringUtils.isBlank(value)) {
            value = context.getRequestParam(name);
        }
        return value;
    }

    private static String getCacheKey(String name) {
        return cachePrefix + name;
    }


}
