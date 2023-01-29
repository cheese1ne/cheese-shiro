package com.cheese.shiro.common.manager.uri;

import com.cheese.shiro.common.Context;
import com.cheese.shiro.common.manager.uri.entity.UriMapping;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;

import java.util.*;

/**
 * uri管理抽象类
 * 负责校验请求uri是否匹配
 * 以及获取 与请求匹配
 * 对应 @Auth @Login @ServerUri 使用不同的管理器，注意进行区分
 *
 * @author sobann
 *
 */
public abstract class UriManager<E extends UriMapping> {

    public static final String REGISTER_PATTERN_PATTERN = "URI_PATTERN";


    protected PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 获取对应服务的UriMappings信息,仅服务端调用
     *
     * @param serviceId spring服务名称
     * @return
     */
    public abstract Collection<E> getUriMappings(String serviceId);


    /**
     * 获取对应服务的UriPatterns，仅服务端使用
     *
     * @param serviceId spring服务名称
     * @return
     */
    public abstract Collection<UriMapping> getUriPatterns(String serviceId);


    /**
     * 请求上下文是否匹配
     *
     * @param context
     * @return
     */
    public boolean isMatch(Context context) {
        E matchUriMapping = getMatchUriMapping(context);
        return matchUriMapping != null;
    }

    /**
     * 请求uri是否匹配
     *
     * @param requestURI
     * @param requestMethod
     * @param appName
     * @return
     */
    public boolean isUriMatch(String requestURI, String requestMethod, String appName) {
        E matchUriMapping = getMatchUriMapping(requestURI, requestMethod, appName);
        return matchUriMapping != null;
    }

    /**
     * 请求uriPattern是否匹配
     *
     * @param uriPattern    请求的uriPattern
     * @param requestMethod
     * @param appName
     * @return
     */
    public boolean isPatternMatch(String uriPattern, String requestMethod, String appName) {
        E mapping = getMatchUriMappingByPattern(uriPattern, requestMethod, appName);
        return mapping != null;
    }

    public E getMatchUriMapping(Context context) {
        //使用uriPattern进行匹配
        String bestUriPattern = context.getBestUriPattern();
        if (bestUriPattern == null) {
            bestUriPattern = getBestMatchPattern(context.getServiceId(), context.getRequestUri(), context.getRequestMethod());
            context.setBestUriPattern(bestUriPattern);
        }
        return getMatchUriMappingByPattern(bestUriPattern, context.getRequestMethod(), context.getServiceId());
    }

    /**
     * 获取匹配的uriPattern
     *
     * @param requestURI
     * @param requestMethod
     * @param serviceId
     * @return
     */
    public E getMatchUriMapping(String requestURI, String requestMethod, String serviceId) {
        if (StringUtils.isBlank(serviceId)) {
            return null;
        }
        Collection<UriMapping> uriPatterns = getUriPatterns(serviceId);
        Collection<E> uriMappings = getUriMappings(serviceId);
        if (CollectionUtils.isEmpty(uriMappings) && CollectionUtils.isEmpty(uriPatterns)) {
            return null;
        }
        return getMatchUriMapping(requestURI, requestMethod, uriMappings, uriPatterns);
    }

    ;

    /**
     * 获取与当前请求匹配的uriMapping
     *
     * @param requestURI    当前请求Uri
     * @param requestMethod 当前请求方式
     * @param uriMappings   适配的UriMapping @Login @Auth等适配信息
     * @param uriPatterns   当前服务中所有的@RequestMapping 路径格式
     * @return 匹配的UriMapping
     */
    public E getMatchUriMapping(String requestURI, String requestMethod, Collection<E> uriMappings, Collection<UriMapping> uriPatterns) {
        String bestMatchPattern = getBestMatchPattern(requestURI, requestMethod, uriPatterns);
        if (bestMatchPattern == null) {
            return null;
        }
        return getMatchUriMapping(requestMethod, bestMatchPattern, uriMappings);
    }

    /**
     * 获取uriPattern对应的uriMapping
     *
     * @param bestMatchPattern
     * @param requestMethod
     * @param serviceId
     * @return
     */
    public E getMatchUriMappingByPattern(String bestMatchPattern, String requestMethod, String serviceId) {
        Collection<E> uriMappings = getUriMappings(serviceId);
        if (CollectionUtils.isEmpty(uriMappings)) {
            return null;
        }
        return getMatchUriMapping(requestMethod, bestMatchPattern, uriMappings);
    }


    /**
     * 获取匹配度最高的 uri pattern
     *
     * @param serviceId
     * @param requestURI
     * @return
     */
    public String getBestMatchPattern(String serviceId, String requestURI, String requestMethod) {
        if (StringUtils.isBlank(serviceId)) {
            return null;
        }
        Collection<UriMapping> uriPatterns = getUriPatterns(serviceId);
        return getBestMatchPattern(requestURI, requestMethod, uriPatterns);
    }


    /**
     * 获取匹配度最高的 uri pattern
     *
     * @param requestURI
     * @param uriPatterns
     * @return
     */
    public String getBestMatchPattern(String requestURI, String requestMethod, Collection<UriMapping> uriPatterns) {
        if (CollectionUtils.isEmpty(uriPatterns)) {
            return null;
        }
        requestMethod = requestMethod.toLowerCase();
        List<String> matchPatterns = new ArrayList<>();
        for (UriMapping uriPattern : uriPatterns) {
            Set<String> patterns = uriPattern.getPatterns();
            Set<String> requestMethods = uriPattern.getRequestMethods();
            boolean matchAllMethod = patterns == null || patterns.size() == 0;
            for (String pattern : patterns) {
                //方法全匹配，仅匹配uri格式
                if (matchAllMethod && pathMatcher.match(pattern, requestURI)) {
                    matchPatterns.add(pattern);
                    //方法存在限制，则先判断方法，再匹配uri格式
                } else if (!matchAllMethod && requestMethods.contains(requestMethod) && pathMatcher.match(pattern, requestURI)) {
                    matchPatterns.add(pattern);
                }
            }
        }
        if (CollectionUtils.isEmpty(matchPatterns)) {
            return null;
        }
        Comparator<String> patternComparator = pathMatcher.getPatternComparator(requestURI);
        matchPatterns.sort(patternComparator);
        return matchPatterns.get(0);
    }

    /**
     * 从uriMappings中获取相应的uriMapping信息
     *
     * @param requestMethod
     * @param bestMatchPattern
     * @param uriMappings
     * @return
     */
    public E getMatchUriMapping(String requestMethod, String bestMatchPattern, Collection<E> uriMappings) {
        if (CollectionUtils.isEmpty(uriMappings) || StringUtils.isBlank(bestMatchPattern)) {
            return null;
        }
        for (E mapping : uriMappings) {
            Set<String> patterns = mapping.getPatterns();
            if (CollectionUtils.isEmpty(patterns)) {
                continue;
            }
            for (String pattern : patterns) {
                boolean is = bestMatchPattern.equals(pattern);
                if (is) {
                    Set<String> requestMethods = mapping.getRequestMethods();
                    //requestMethod为空，默认所有请求
                    if (CollectionUtils.isEmpty(requestMethods)) {
                        return mapping;
                    }
                    //查找是否为对应请求
                    for (String method : requestMethods) {
                        if (method.equalsIgnoreCase(requestMethod)) {
                            return mapping;
                        }
                    }
                }
            }
        }
        return null;
    }


}
