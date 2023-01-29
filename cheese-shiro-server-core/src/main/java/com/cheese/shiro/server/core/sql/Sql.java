package com.cheese.shiro.server.core.sql;

/**
 * sql语句对象包装类
 * @author sobann
 */
public class Sql {
	private String stmt;

	public Sql() {
	}

	public Sql(String stmt) {
		this.stmt = stmt;
	}

	public String getStmt() {
		return stmt;
	}

	public void setStmt(String stmt) {
		this.stmt = stmt;
	}
	
}
