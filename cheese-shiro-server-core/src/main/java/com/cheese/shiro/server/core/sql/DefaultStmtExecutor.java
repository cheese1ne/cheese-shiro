package com.cheese.shiro.server.core.sql;

import com.cheese.shiro.common.domain.SPage;
import com.cheese.shiro.common.table.EntityInfo;
import com.cheese.shiro.common.util.SqlUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 默认的sql语句执行器
 *
 * @author sobann
 */
public class DefaultStmtExecutor implements StmtExecutor {
    private static final Logger logger = LoggerFactory.getLogger(DefaultStmtExecutor.class);
    private DataSource dataSource;
    private String service;

    public DefaultStmtExecutor(String service, DataSource dataSource) {
        this.dataSource = dataSource;
        this.service = service;
    }

    @Override
    public Set<String> executeSqlForIds(String stmt, Object[] params) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(stmt);
            logger.info("DefaultStmtExecutor-{} ==> Prepare SQL :{}", service, stmt);
            prepareParams(statement, params);
            resultSet = statement.executeQuery();
            Set<String> results = new HashSet<>();
            while (resultSet.next()) {
                results.add(resultSet.getString(1));
            }
            logger.info("DefaultStmtExecutor-{} <== Total : {}", service, results.size());
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, statement, resultSet);
        }
    }

    private void close(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareParams(PreparedStatement statement, Object[] params) throws SQLException {
        if (params != null && params.length > 0) {
            StringBuilder sb = new StringBuilder("DefaultStmtExecutor-" + service + " ==> SQL Params : ");
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                statement.setObject(i + 1, param);
                sb.append(param);
                sb.append("(");
                sb.append(param.getClass().getSimpleName());
                sb.append("),");
            }
            logger.info(sb.substring(0, sb.length() - 1));
        }
    }

    @Override
    public List<EntityInfo> getEntityInfoFromSql(String stmt, Object[] params) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(stmt);
            logger.info("DefaultStmtExecutor-{} ==> Prepare SQL :{}", service, stmt);
            prepareParams(statement, params);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<EntityInfo> results = new ArrayList<>();
            while (resultSet.next()) {
                EntityInfo entity = new EntityInfo();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = resultSet.getString(i);
                    entity.addProp(columnName, value);
                }
                entity.setId(entity.getProp().remove("id"));
                entity.setName(entity.getProp().remove("name"));
                entity.setPid(entity.getProp().remove("pid"));
                results.add(entity);
            }
            logger.info("DefaultStmtExecutor-{} <== Total : {}", service, results.size());
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, statement, resultSet);
        }
    }

    @Override
    public SPage<EntityInfo> getPageOfEntityInfo(String stmt, Object[] params, int page, int size) {
        try {
            Integer total = getTotal(stmt, params);
            if (total == null || total == 0) {
                return new SPage<>(page, size, 0, new ArrayList<>());
            }
            PlainSelect plainSelect = SqlUtil.getPlainSelect(stmt);
            Limit limit = new Limit();
            limit.setRowCount(new LongValue(size));
            limit.setOffset(new LongValue((page - 1) * size));
            plainSelect.setLimit(limit);
            List<EntityInfo> entitys = getEntityInfoFromSql(plainSelect.toString(), params);
            return new SPage<>(page, size, total, entitys);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getTotal(String stmt, Object[] params) throws JSQLParserException {
        PlainSelect plainSelect = SqlUtil.getPlainSelect(stmt);
        List<SelectItem> counts = countSelectItem("*");
        plainSelect.setSelectItems(counts);
        plainSelect.setOrderByElements(null);
        plainSelect.setLimit(null);
        String sql = plainSelect.toString();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            logger.info("DefaultStmtExecutor-{} ==> Prepare SQL :{}", service, sql);
            prepareParams(statement, params);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int total = resultSet.getInt(1);
                logger.info("DefaultStmtExecutor-{} <== Total : {}", service, total);
                return total;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(connection, statement, resultSet);
        }
    }

    private List<SelectItem> countSelectItem(String countColumn) {
        Function function = new Function();
        function.setName("COUNT");
        List<Expression> expressions = new ArrayList<>();
        Column column = new Column(countColumn);
        ExpressionList expressionList = new ExpressionList();
        expressions.add(column);
        expressionList.setExpressions(expressions);
        function.setParameters(expressionList);
        List<SelectItem> selectItems = new ArrayList<>();
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem(function);
        selectItems.add(selectExpressionItem);
        return selectItems;
    }
}
