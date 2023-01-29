package com.cheese.shiro.common.manager.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * 用户会话管理器
 * 对在线用户进行管理和统计
 *
 * @author sobann
 */
public interface SessionStoreManager {
    Logger logger = LoggerFactory.getLogger(SessionStoreManager.class);

    /**
     * 获取session
     *
     * @param key
     * @return
     */
    Session get(String key);

    /**
     * 存储session
     *
     * @param key
     * @param session
     */
    void save(String key, Session session);

    /**
     * 删除Session
     *
     * @param key
     */
    void clear(String key);

    /**
     * 获取所有的活动会话
     *
     * @return
     */
    Collection<Session> getLives();

    /**
     * 获取当前所有活动会话数量
     *
     * @return
     */
    int getLiveNum();

    /**
     * 确认会话是否存活
     *
     * @param key
     * @return
     */
    Boolean isLive(String key);

    /**
     * 获取会话中存在的值
     *
     * @param key
     * @param prop
     * @return
     */
    Object getProp(String key, String prop);

    /**
     * 存储参数
     *
     * @param key
     * @param prop
     * @param value
     */
    void saveProp(String key, String prop, Object value);

    /**
     * 删除参数
     *
     * @param key
     * @param prop
     * @return
     */
    Object removeProp(String key, String prop);

    /**
     * 获取参数hash表
     *
     * @param key
     * @return
     */
    Map<String, Object> getProps(String key);

    /**
     * 保存参数hash表
     *
     * @param key
     * @param props
     */
    void saveProps(String key, Map<String, Object> props);

    /**
     * 设置session过期时间，应该与token一致，或比token稍长
     *
     * @param min
     */
    void setExpire(long min);

    /**
     * 获取session生命周期
     *
     * @return
     */
    long getExpire();


}
