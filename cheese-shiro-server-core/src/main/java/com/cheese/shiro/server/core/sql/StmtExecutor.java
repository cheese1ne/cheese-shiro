package com.cheese.shiro.server.core.sql;


import com.cheese.shiro.common.domain.SPage;
import com.cheese.shiro.common.table.EntityInfo;

import java.util.List;
import java.util.Set;


/**
 * sql执行器
 * @author sobann
 */
public interface StmtExecutor {
	/**
	 * 执行sql语句，结果集为id
	 * @param stmt sql语句
	 * @param params 条件
	 * @return
	 */
	Set<String> executeSqlForIds(String stmt, Object[] params);

	/**
	 *  执行sql语句，返回 EntityInfo集合
	 * @param stmt 语句
	 * @param params 条件
	 * @return
	 */

	List<EntityInfo> getEntityInfoFromSql(String stmt, Object[] params);

	/**
	 * 对原有sql语句进行分页查询
	 * @param stmt 语句
	 * @param params 查询条件
	 * @param page 第几页
	 * @param size 每页条数
	 * @return
	 */
	SPage<EntityInfo> getPageOfEntityInfo(String stmt, Object[] params, int page, int size);
}
