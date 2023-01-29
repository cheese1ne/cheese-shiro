package com.cheese.shiro.common.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 * 属性转换器，使用ForestManager改造
 *
 * @author sobann
 */
public class TreeUtils {
    /**
     * 批量获取节点 所有子级id
     *
     * @param currentIds 开始查询的节点ids
     * @param treeIds    整个查询范围的树形id
     * @param <T>
     * @return 所有子级节点id，不包括currentIds
     */
    public static <T> List<T> getChildrenList(Collection<T> currentIds, Map<T, T> treeIds) {

        List<T> children = new ArrayList<>();
        if (treeIds == null || treeIds.size() == 0 || currentIds == null || currentIds.size() == 0) {
            return children;
        }

        //解决遍历删除异常，保持原有数据
        Map<T, T> map = new ConcurrentHashMap<>();
        map.putAll(treeIds);

        //保持原有数据
        List<T> currentparentIds = new ArrayList<>();
        currentparentIds.addAll(currentIds);

        while (true) {
            List<T> currentChildrens = new ArrayList<>();
            for (Map.Entry<T, T> tree : map.entrySet()) {
                if (currentparentIds.contains(tree.getValue())) {
                    //把当前循环下的子级加入进行
                    currentChildrens.add(tree.getKey());
                    //将已经加入的元素去除，减少循环次数,同时去重
                    map.remove(tree.getKey());
                }
            }
            if (currentChildrens.size() == 0) {
                //不存在子级，停止循环
                break;
            } else {
                //重置下一次循环的父级
                currentparentIds.clear();
                currentparentIds.addAll(currentChildrens);
                //将本次循环的子级结果，加入集合中
                children.addAll(currentChildrens);
            }

        }
        return children;

    }

    /**
     * 查询单个节点所有子级节点id
     *
     * @param currentId 开始节点id
     * @param treeIds   树形范围ids
     * @param <T>
     * @return 所有子级节点 （不包括开始节点）
     */
    public static <T> List<T> getChildrenList(T currentId, Map<T, T> treeIds) {
        List<T> children = new ArrayList<>();
        if (treeIds == null || treeIds.size() == 0 || !treeIds.values().contains(currentId)) {
            return children;
        }
        List<T> currentIds = new ArrayList<>();
        currentIds.add(currentId);
        return getChildrenList(currentIds, treeIds);
    }


    /**
     * 获取单个节点的所有父节点ids
     *
     * @param currentId 当前开始节点id
     * @param treeIds   整个树形ids
     * @param rootId    树形顶端id(0)
     * @param <T>
     * @return 所有父级节点ids
     */
    public static <T> List<T> getParentList(T currentId, Map<T, T> treeIds, T rootId) {
        List<T> parentIds = new ArrayList<>();
        if (treeIds == null || treeIds.size() == 0 || !treeIds.keySet().contains(currentId)) {
            return parentIds;
        }
        while (true) {
            if (treeIds.keySet().contains(currentId)) {
                T currentParent = treeIds.get(currentId);
                if (!currentParent.equals(rootId)) {
                    parentIds.add(currentParent);
                    //替换下次循环的子级
                    currentId = currentParent;
                } else {
                    //遍历至根节点停止
                    break;
                }
            } else {
                //不再拥有父级
                break;
            }
        }
        return parentIds;
    }

    /**
     * @param currentId 当前查询节点
     * @param treeIds   所有树形节点id
     * @param rootId    树根Id
     * @param index     第几级父级
     * @param <T>
     * @return
     */
    public static <T> T getParent(T currentId, Map<T, T> treeIds, T rootId, int index) {
        List<T> parentList = getParentList(currentId, treeIds, rootId);
        if (parentList == null || parentList.size() == 0 || parentList.size() - 1 - index < 0) {
            return null;
        }
        return parentList.get(parentList.size() - 1 - index);
    }

    /**
     * 获取当前ids的所有父级
     * 结果已经去重
     *
     * @param currentIds
     * @param treeIds
     * @param <T>
     * @return
     */
    public static <T> List<T> getParentList(Collection<T> currentIds, Map<T, T> treeIds, T rootId) {
        List<T> parentIds = new ArrayList<>();
        if (treeIds == null || treeIds.size() == 0 || currentIds == null || currentIds.size() == 0) {
            return parentIds;
        }
        Map<T, T> map = new ConcurrentHashMap<>();
        map.putAll(treeIds);
        List<T> currentChildrenIds = new ArrayList<>();
        currentChildrenIds.addAll(currentIds);
        //结果进行去重
        Set<T> parentSet = new HashSet<>();
        while (true) {
            List<T> currentParentIds = new ArrayList<>();
            for (Map.Entry<T, T> tree : map.entrySet()) {
                if (currentChildrenIds.contains(tree.getKey())) {
                    T currentParent = tree.getValue();
                    //不计入跟节点
                    if (!currentParent.equals(rootId)) {
                        currentParentIds.add(tree.getValue());
                    }
                    map.remove(tree.getKey());
                }
            }
            if (currentParentIds.size() > 0) {
                parentSet.addAll(currentParentIds);
                currentChildrenIds.clear();
                currentChildrenIds.addAll(currentParentIds);
            } else {
                break;
            }
        }
        parentIds.addAll(parentSet);
        return parentIds;
    }

}
