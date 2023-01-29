package com.cheese.shiro.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 集合工具
 * @author sobann
 */
public class ListUtil {

    public static <T> List<T> asList(T t){
        List<T> list  = new ArrayList<>(1);
        list.add(t);
        return list;
    }

    public static List<Integer> parseStrIds(String idsStr){
        List<Integer> ids = new ArrayList<>();
        if(StringUtils.isBlank(idsStr)){
            return ids;
        }
        String[] split = idsStr.split(",");
        for (String idStr : split) {
            try {
                ids.add(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return ids;
    }

    public static  List<Integer> parseStrIds(Collection<String> idsStr){
        List<Integer> ids = new ArrayList<>();
        if(CollectionUtils.isEmpty(idsStr)){
            return ids;
        }
        for (String idStr : idsStr) {
            try {
                ids.add(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return ids;
    }

    /**
     * list 结构转换为 嵌套树
     * @param list  元素集合
     * @param id   树形节点id标识（json）
     * @param pid  树形节点父级id标识(json)
     * @param children  子级节点标识（json）
     * @param clazz 最后序列化的类型
     * @param <T>
     * @return
     */
    public static <T> List<T> listToTree(List list,String id,String pid,String children,Class<T> clazz){
        JSONArray result = listToTree(JSONArray.parseArray(JSON.toJSONString(list)),id,pid,children);
        return JSON.parseArray(result.toJSONString(), clazz);
    }

    /**
     * listToTree
     * <p>方法说明<p>
     * 将JSONArray数组转为树状结构
     * @param arr 需要转化的数据
     * @param id 数据唯一的标识键值
     * @param pid 父id唯一标识键值
     * @param children 子节点键值
     * @return JSONArray
     */
    public static JSONArray listToTree(JSONArray arr, String id, String pid, String children){
        JSONArray r = new JSONArray();
        JSONObject hash = new JSONObject();
        //将数组转为Object的形式，key为数组中的id
        for(int i=0;i<arr.size();i++){
            JSONObject json = (JSONObject) arr.get(i);
            hash.put(json.getString(id), json);
        }
        //遍历结果集
        for(int j=0;j<arr.size();j++){
            //单条记录
            JSONObject aVal = (JSONObject) arr.get(j);
            //在hash中取出key为单条记录中pid的值
            JSONObject hashVP = (JSONObject) hash.get(aVal.get(pid).toString());
            //如果记录的pid存在，则说明它有父节点，将她添加到孩子节点的集合中
            if(hashVP!=null){
                //检查是否有child属性
                if(hashVP.get(children)!=null){
                    JSONArray ch = (JSONArray) hashVP.get(children);
                    ch.add(aVal);
                    hashVP.put(children, ch);
                }else{
                    JSONArray ch = new JSONArray();
                    ch.add(aVal);
                    hashVP.put(children, ch);
                }
            }else{
                r.add(aVal);
            }
        }
        return r;
    }


    /**
     * 将嵌套树形结构转换为list
     * @param tree
     * @param children
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> treeToList(Collection tree, String children, Class<T> clazz){
        return treeToList(tree,children,clazz,null,null,null,null);
    }

    /**
     * 将嵌套树形结构转换为list,并对排序进行赋值
     * @param tree
     * @param children
     * @param clazz
     * @param sort = null 不进行排序
     * @param <T>
     * @return
     */
    public static <T> List<T> treeToList(Collection tree, String children, Class<T> clazz,String sort ){
        return treeToList(tree,children,clazz,sort,null,null,null);
    }

    /**
     * 将嵌套树形结构转换为list
     * 并按照嵌套结构，对父子级进行更改，进行排序
     * @param tree
     * @param children
     * @param clazz
     * @param sort = null不进行排序
     * @param id
     * @param pid
     * @param rootId
     * @param <T>
     * @return
     */
    public static <T> List<T> treeToList(Collection tree, String children,Class<T> clazz, String sort, String id,String pid,Object rootId){
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(tree));
        JSONArray result = treeToList(jsonArray ,children,sort,id,pid,rootId);
        return JSON.parseArray(result.toJSONString(),clazz);
    }

    public static JSONArray treeToList(JSONArray elements,String child,String sort,String idName,String pidName,Object rootId){
        JSONArray array = new JSONArray();
        if(StringUtils.isBlank(idName)||StringUtils.isBlank(pidName)||rootId==null){
            listChildrenWithRecursion(elements,array,child,sort);
        }else{
            setChildrenWithRecursion(elements,array,child,sort,idName,pidName,rootId);
        }
        return array;
    }

    private static void setChildrenWithRecursion(JSONArray elements,JSONArray result,String childName,String sortName,String id,String pid,Object parentId){
        boolean setSort = true;
        if(StringUtils.isBlank(sortName)){
            setSort=false;
        }
        for (int i = 0; i < elements.size(); i++) {
            JSONObject jsonObject = (JSONObject) elements.get(i);
            Object children = jsonObject.remove(childName);
            jsonObject.put(childName,new JSONArray());
            jsonObject.put(pid,parentId);
            if(setSort){
                jsonObject.put(sortName,i+1);
            }
            result.add(jsonObject);
            if(children!=null){
                JSONArray jsonArray = (JSONArray) children;
                if(jsonArray.size()>0){
                    setChildrenWithRecursion(jsonArray,result,childName,sortName,id,pid,jsonObject.get(id));
                }
            }
        }
    }

    /**
     *
     * @param elements
     * @param result
     * @param childName
     * @param sortName
     */
    private static void listChildrenWithRecursion(JSONArray elements,JSONArray result,String childName,String sortName){
        boolean setSort = true;
        if(StringUtils.isBlank(sortName)){
            setSort=false;
        }
        for (int i = 0; i < elements.size(); i++) {
            JSONObject jsonObject = (JSONObject) elements.get(i);
            Object children = jsonObject.remove(childName);
            jsonObject.put(childName,new JSONArray());
            if(setSort){
                jsonObject.put(sortName,i+1);
            }
            result.add(jsonObject);
            if(children!=null){
                JSONArray jsonArray = (JSONArray) children;
                if(jsonArray.size()>0){
                    listChildrenWithRecursion(jsonArray,result,childName,sortName);
                }
            }
        }
    }


}
