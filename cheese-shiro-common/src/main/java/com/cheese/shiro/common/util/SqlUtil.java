package com.cheese.shiro.common.util;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * jsqlparser 的sql 语句工具
 * @author 磊
 *
 */
public class SqlUtil {


	/**
	 * 为sql语句添加 集合 过滤条件
	 * @param sql 原有sql
	 * @param filterColumn 进行过滤的列名
	 * @param values  集合值
	 * @param isStr 过滤列是否为字符串
	 * @return
	 * @throws JSQLParserException
	 */
	public static String addInCondition(String sql, String filterColumn, Collection<? extends Serializable> values, boolean isStr) throws JSQLParserException {
		PlainSelect plainSelect = getPlainSelect(sql);
		Expression expression = buildInOrEqualExpress(filterColumn, values, isStr);
		setWhereCondition(plainSelect,expression);
		return plainSelect.toString();
	}


	/**
	 * 添加子查询条件 + colunm in(select * from );
	 * @param sql 原有语句
	 * @param filterColumn 进行过滤的列名
	 * @param subSql 子查询语句
	 * @return
	 * @throws JSQLParserException
	 */
	public static String addSubSqlCondition(String sql,String filterColumn,String subSql) throws JSQLParserException {
		PlainSelect selectBody = getPlainSelect(sql);
		PlainSelect subBody = getPlainSelect(subSql);
		InExpression inExpression = new InExpression();
		Column column = new Column(filterColumn);
		inExpression.setLeftExpression(column);
		SubSelect subSelect = new SubSelect();
		subSelect.setSelectBody(subBody);
		inExpression.setRightItemsList(subSelect);
		setWhereCondition(selectBody, inExpression);
		return selectBody.toString();
	}

	public static Expression buildStringExpress(String condition) throws JSQLParserException {
		return CCJSqlParserUtil.parseCondExpression(condition);
	}


	public static Expression buildInOrEqualExpress(String filterColumn, Collection<? extends Serializable> values, boolean isStr){
		if(values.size()==1){
			Serializable value = values.iterator().next();
			return buildEqualExpress(filterColumn,value,isStr);
		}else {
			return buildInExpress(filterColumn,values,isStr);
		}
	}

	public static Expression buildInExpress(String filterColumn, Collection<? extends Serializable> values, boolean isStr){
		ExpressionList expressionList = parseCollection(values, isStr);
		InExpression inExpression = new InExpression();
		Column column = new Column(filterColumn);
		inExpression.setLeftExpression(column);
		inExpression.setRightItemsList(expressionList);
		return inExpression;
	}

	public static Expression buildEqualExpress(String filterColumn,Serializable value,boolean idStr){
		EqualsTo equalsTo = new EqualsTo();
		equalsTo.setLeftExpression(new Column(filterColumn));
		if(idStr){
			equalsTo.setRightExpression(new StringValue(""+value));
		}else{
			equalsTo.setRightExpression(new LongValue(""+value));
		}
		return equalsTo;
	}

	public static Expression buildOrExpress(List<Expression> expressions){
		OrExpression orExpression = null;
		Expression pre = null;
		for (int i = 0; i < expressions.size(); i++) {
			if(i==0){
				pre = expressions.get(i);
			}else {
				orExpression = new OrExpression(pre,expressions.get(i));
				pre = orExpression;
			}
		}
		return orExpression==null ? pre:orExpression;
	}

	public static Expression buildAndExpress(List<Expression> expressions){
		AndExpression andExpression = null;
		Expression pre = null;
		for (int i = 0; i < expressions.size(); i++) {
			if(i==0){
				pre = expressions.get(i);
			}else {
				andExpression = new AndExpression(pre,expressions.get(i));
				pre = andExpression;
			}
		}
		return andExpression==null ? pre:andExpression;
	}



	public static String addCondition(String sql,Expression expression) throws JSQLParserException {
		PlainSelect plainSelect = getPlainSelect(sql);
		setWhereCondition(plainSelect, expression);
		return plainSelect.toString();
	}

	/**
	 * 添加条件  where type=1
	 * @param sql 原有语句
	 * @param condition 条件 type=1
	 * @return
	 * @throws JSQLParserException
	 */
	public static String addCondition(String sql,String condition) throws JSQLParserException {
		PlainSelect plainSelect = getPlainSelect(sql);
		Expression expression = CCJSqlParserUtil.parseCondExpression(condition);
		setWhereCondition(plainSelect, expression);
		return plainSelect.toString();
	}

	/**
	 * 解析sql语句
	 * @param sql
	 * @return
	 * @throws JSQLParserException
	 */
	public static PlainSelect getPlainSelect(String sql) throws JSQLParserException {
		Select parse =(Select) CCJSqlParserUtil.parse(sql);
		PlainSelect selectBody = (PlainSelect) parse.getSelectBody();
		return selectBody;
	}

	/**
	 * 设置、添加 where条件
	 * @param plainSelect
	 * @param where
	 * @return
	 * @throws JSQLParserException
	 */
	public static PlainSelect setWhereCondition(PlainSelect plainSelect, Expression where) throws JSQLParserException {
		Expression original = plainSelect.getWhere();
		if(original==null){
			plainSelect.setWhere(where);
		}else {
			original = CCJSqlParserUtil.parseCondExpression("("+original+")");
			Expression total = new AndExpression(original,where);
			plainSelect.setWhere(total);
		}
		return plainSelect;
	}


	/**
	 * 解析呈条件表达式
	 * @param ids
	 * @return
	 */
	public static ExpressionList parseCollection(Collection<? extends Serializable> ids, boolean isStr){
		List<Expression> expressions = new ArrayList<>();
		if(isStr){
			for (Serializable id : ids) {
				expressions.add(new StringValue(id+""));
			}
		}else{
			for (Serializable id : ids) {
				expressions.add(new LongValue(id+""));
			}
		}
		return new ExpressionList(expressions);
	}

	/**
	 * 作废
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static String invalid(String sql) throws Exception{
		Select parse =(Select) CCJSqlParserUtil.parse(sql);
        PlainSelect selectBody =(PlainSelect) parse.getSelectBody();
        Expression invalid = CCJSqlParserUtil.parseCondExpression(" 1=0 ");
        Expression where = selectBody.getWhere();
        if(where == null){
        	selectBody.setWhere(invalid);
        }else{
        	where = CCJSqlParserUtil.parseCondExpression("("+where+")");
        	selectBody.setWhere(new AndExpression(where, invalid));
        }

		return selectBody.toString();
	}

	public static String addResultCondition(String sql, String instanceId) throws JSQLParserException {
		PlainSelect plainSelect = getPlainSelect(sql);
		SelectExpressionItem expressionItem =(SelectExpressionItem) plainSelect.getSelectItems().iterator().next();
		Expression expression = CCJSqlParserUtil.parseCondExpression(expressionItem.getExpression()+"="+instanceId);
		setWhereCondition(plainSelect,expression);
		return plainSelect.toString();
	}

    /**
	 * 将 ids集合转化为
	 * @param targetIds
	 * @return
	 */
//	public static String buildCollectionToString(Collection<? extends Serializable> targetIds,boolean isStr) {
//		StringBuffer ids =new StringBuffer("");
//		int i = 0;
//		int size = targetIds.size() -1;
//		if(!isStr){
//			for (Serializable id : targetIds) {
//				if(i!= size){
//					ids.append(id+",");
//				}else{
//					ids.append(id);
//				}
//				i++;
//			}
//		}else{
//			for (Serializable id : targetIds) {
//				if(i!= size){
//					ids.append("'"+id+"',");
//				}else{
//					ids.append("'"+id+"'");
//				}
//				i++;
//			}
//		}
//		return ids.toString();
//	}
}
