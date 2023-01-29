package com.cheese.shiro.common.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 将权限还原成初始条件
 * predefine:dept:parent:2(hash:dept:child:2)&propertity:metadata:mj>2
 * hash:metadata:self:020202030002330,020202030002331
 * 外层list为或
 * 内层list为且
 * conditions 转换为

 *  [
        [
            {"name":"dept","value":[2,21,22]},
            {"name":"metadata","value":" mj > 2"}
        ],
        [
            {"name":"metadata","value":["020202030002330","020202030002331"]}
        ]
    ]
 *
 * @author sobann
 */
public class QueryCondition implements Serializable {
    private static final long serialVersionUID = 1583775642012553142L;
    private boolean all = false;
    private List<List<FieldValue>> conditions = new ArrayList<>();

    public QueryCondition() {
    }

    public QueryCondition(boolean all) {
        this.all = all;
    }

    public QueryCondition(boolean all, List<List<FieldValue>> conditions) {
        this.all = all;
        this.conditions = conditions;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public List<List<FieldValue>> getConditions() {
        return conditions;
    }

    public void setConditions(List<List<FieldValue>> conditions) {
        this.conditions = conditions;
    }

    public List<List<FieldValue>> addCondition(List<FieldValue> condition){
        this.conditions.add(condition);
        return this.conditions;
    }
}
