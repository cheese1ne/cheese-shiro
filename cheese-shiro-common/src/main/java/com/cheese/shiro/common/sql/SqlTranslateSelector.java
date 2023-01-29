package com.cheese.shiro.common.sql;

/**
 * sql转换选择器，权限控制的核心
 * 通过在权限服务注册多数据源，在进行权限访问时
 *
 * @author sobann
 */
public interface SqlTranslateSelector {
    /**
     * 根据实体获取数据转换器
     *
     * @param target
     * @return
     */
    SqlTranslate getSqlTranslate(String target);

    /**
     * 根据实体及转换标识获取转换器，在查询权限信息属于需要进行转换时使用
     * 如查询某一类实体数据用户所属单位的数据权限
     *
     * @param target
     * @param scope
     * @return
     */
    SqlTranslate getSqlTranslate(String target, String scope);
}
