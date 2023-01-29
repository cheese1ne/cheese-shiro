package com.cheese.shiro.server.core.props;

import com.cheese.shiro.server.core.config.DataSourceConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 权限服务端配置信息：
 * 权限客户端的数据源配置信息及权限服务接口暴露方式
 * @author sobann
 */
@Component
@ConfigurationProperties("cheese.shiro.server")
public class ShiroServerCoreProps {
    /**
     * 服务暴露方式
     * mvc
     * jersey
     * dubbo
     * none
     */
    private String exposer ="none";

    /**
     * selector实现类
     * default
     * multi
     */
    private String selector ="default";

    /**
     * 批量检查数量
     */
    private int batchSize=5;

    /**
     * 默认数据源
     */
    private String defaultDataSource;

    /**
     * 数据源键值列表
     */
    private Map<String, DataSourceConfig> dataSources;

    public String getExposer() {
        return exposer;
    }

    public void setExposer(String exposer) {
        this.exposer = exposer;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getDefaultDataSource() {
        return defaultDataSource;
    }

    public void setDefaultDataSource(String defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    public Map<String, DataSourceConfig> getDataSources() {
        return dataSources;
    }

    public void setDataSources(Map<String, DataSourceConfig> dataSources) {
        this.dataSources = dataSources;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
