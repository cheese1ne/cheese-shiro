package com.cheese.shiro.server.core.manager.rule.parser;

import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.sql.SqlTranslate;
import com.cheese.shiro.common.sql.SqlTranslateSelector;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author sobann
 */
public class DegradeUtils {
    public static void degradeScope(String scope, Collection<String> scopeIds, List<String> scopes, QueryMap queryMap, SqlTranslateSelector sqlTranslateSelector){
        if(CollectionUtils.isEmpty(scopeIds)){
            return;
        }
        if(scopes.contains(scope)){
            queryMap.addScope(scope,scopeIds);
        }else {
            for (String adapterScope : scopes) {
                SqlTranslate sqlTranslate = sqlTranslateSelector.getSqlTranslate(adapterScope, scope);
                //按照顺序，转换成第一个可转换范围
                if(sqlTranslate!=null && sqlTranslate.isContainer(adapterScope,scope)){
                    SqlTranslate adapterTranslate = sqlTranslateSelector.getSqlTranslate(adapterScope, scope);
                    String sql = adapterTranslate.buildSql(adapterScope, scope, scopeIds);
                    Set<String> adaperIds = adapterTranslate.querySql(sql);
                    queryMap.addScope(adapterScope, adaperIds);
                    return;
                }
            }
        }
    }
}
