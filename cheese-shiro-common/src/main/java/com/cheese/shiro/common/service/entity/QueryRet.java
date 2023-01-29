package com.cheese.shiro.common.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限查询结果
 *
 * @author sobann
 */
public class QueryRet implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 是否是全部结果
     */
    private boolean all = false;
    /**
     * 实例id类型是否为字符串
     */
    private boolean str = false;
	/**
	 * 实例id集合
	 */
	private List<String> entity = new ArrayList<>();

    public QueryRet() {
        super();
    }


    public QueryRet(boolean all, boolean isStr) {
        super();
        this.all = all;
        this.str = isStr;
    }


    public QueryRet(List<String> entity, boolean isStr) {
        super();
        this.entity = entity;
        this.str = isStr;
    }

    public QueryRet(boolean all, List<String> entity, boolean isStr) {
        super();
        this.all = all;
        this.entity = entity;
        this.str = isStr;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public boolean isStr() {
        return str;
    }

    public void setStr(boolean str) {
        this.str = str;
    }

    public List<String> getEntity() {
        return entity;
    }

    public void setEntity(List<String> entity) {
        this.entity = entity;
    }

    public boolean isEmpty() {
    	if (!all){
			return entity == null || entity.size() == 0;
		}
        return false;
    }

    public static QueryRet intersection(QueryRet one, QueryRet other) {
        if (one == null || other == null || one.isEmpty() || other.isEmpty()) {
            return new QueryRet();
        }
        if (one.isAll()) {
            return other;
        } else if (other.isAll()) {
            return one;
        } else {
            List<String> entityOne = one.getEntity();
            List<String> entityOther = other.getEntity();
            List<String> intersection = entityOne.stream().filter(entityOther::contains).collect(Collectors.toList());
            return new QueryRet(intersection, one.isStr());
        }
    }
}
