package com.cheese.shiro.client.core.register;


import com.cheese.shiro.common.manager.uri.UriManager;
import com.cheese.shiro.common.manager.uri.entity.UriMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * uri注册器默认接口
 *
 * @param <E>
 * @author sobann
 */
public interface UriConfigRegister<E extends UriMapping> extends ConfigRegister {
    /**
     * 获取注册配置信息
     *
     * @return
     */
    @Override
    default Map<String, Object> getRegisterConfig() {
        Map<String, Object> uris = new HashMap<>();
        List<E> uriMappings = getUriMappingsForRegister();
        uris.put(getUriRegisterKey(), uriMappings);
        List<UriMapping> uriPatterns = getUriPatternsForRegister();
        uris.put(UriManager.REGISTER_PATTERN_PATTERN, uriPatterns);
        return uris;
    }

    /**
     * 仿照MVC框架，获取对应的uri资源路径信息
     *
     * @return
     */
    List<E> getUriMappingsForRegister();

    /**
     * 仿照MVC框架，获取uri路径对象
     *
     * @return
     */
    List<UriMapping> getUriPatternsForRegister();

    /**
     * uri资源路径类型
     *
     * @return
     */
    String getUriRegisterKey();
}
