package com.cheese.shiro.common;

/**
 * 用于统一zuul gateway网关的接口类
 * 对网关上下文进行包装，实现该接口
 *
 * @author sobann
 */
public interface Context<E> {
    /**
     * 错误响应内容
     */
    String ERROR_CONTENT = "errorContent";
    /**
     * 错误响应格式
     */
    String ERROR_CONTENT_TYPE = "errorContentType";
    /**
     * 身份id
     */
    String IDENTITY_CONTEXT = "identity";
    /**
     * 当前请求匹配的urlPattern
     */
    String BEST_URI_PATTERN = "bestUriPattern";

    /**
     * 客户端Ip
     */
    String REAL_IP = "realIp";

    /**
     * 获取当前请求的对应服务
     *
     * @return
     */
    String getServiceId();

    /**
     * 获取当前请求客户端的真实IP
     *
     * @return
     */
    String getRealIp();

    /**
     * 获取当前请求的路径
     * 经处理后，即在对应服务中的真实URI资源路径
     *
     * @return
     */
    String getRequestUri();

    /**
     * 当前请求匹配的urlPattern
     *
     * @return
     */
    String getBestUriPattern();

    /**
     * 设置当前请求匹配的urlPattern
     *
     * @param bestUriPattern
     */
    void setBestUriPattern(String bestUriPattern);

    /**
     * 获取当前请求方式
     *
     * @return
     */
    String getRequestMethod();

    /**
     * 获取请求体内容
     * 仅在json/formData格式中生效
     *
     * @return
     */
    String getRequestBody();

    /**
     * 获取请求提交格式
     *
     * @return
     */
    String getRequestContentType();


    /**
     * 获取请求参数 query或formdata中的参数值
     *
     * @param name 参数名称
     * @return
     */
    String getRequestParam(String name);

    /**
     * 获取请求头
     *
     * @param name
     * @return
     */
    String getRequestHeader(String name);

    /**
     * 获取cookie
     *
     * @param name
     * @return
     */
    String getRequestCookie(String name);

    /**
     * 获取 发生 错误时的相应内容
     *
     * @return null或空字符串，代表无错误
     */
    String getErrorContent();

    /**
     * 设置 发生 错误时内容
     *
     * @param content
     */
    void setErrorContent(String content);

    /**
     * 获取错误内容格式
     *
     * @return Content-Type: application/json;charset=UTF-8
     */
    String getErrorContentType();

    /**
     * 设置错误内容格式
     *
     * @param type
     */
    void setErrorContentType(String type);

    /**
     * 获取当前身份信息
     *
     * @return
     */
    Object getIdentityContext();

    /**
     * 设置当前身份信息
     *
     * @param context
     * @return
     */
    void setIdentityContext(Object context);

    /**
     * 添加请求头信息
     *
     * @param name
     * @param value
     */
    void addRequestHeader(String name, String value);

    /**
     * 添加响应头信息
     *
     * @param name
     * @param value
     */
    void addResponseHeader(String name, String value);

    /**
     * 响应头添加cookie发送给客户端浏览器
     *
     * @param name
     * @param value
     * @param domian
     * @param path
     */
    void addCookie(String name, String value, String domian, String path);

    /**
     * 获取 原始上下文
     *
     * @return
     */
    E getOriginalContext();

    /**
     * 获取特征值
     *
     * @param key
     * @return
     */
    Object getAttribute(String key);

    /**
     * 存储特征值
     *
     * @param key
     * @param value
     */
    void setAttribute(String key, Object value);

}
