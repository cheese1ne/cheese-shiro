package com.cheese.shiro.server.gateway.util;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 使用FNV1_32_HASH算法计算服务器的Hash值
 * @author sobann
 */
public class HashLoadBalanceUtil {

    private HashLoadBalanceUtil(){}

    private static int getHash(String key){
        final int p = 16777619;
        int hash = (int)2166136261L;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash ^ key.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

    public static ServiceInstance getInstance(String key, List<ServiceInstance> instances, int virtualNum){
        if(instances.size()==1){
            return instances.get(0);
        }
        //得到该key的hash值
        int hash = getHash(key);
        // 得到大于该Hash值的所有Map
        SortedMap<Integer, ServiceInstance> virtualNodes = createNodes(instances,virtualNum);
        SortedMap<Integer, ServiceInstance> sortedMap = virtualNodes.tailMap(hash);
        if(sortedMap.isEmpty()){
            //如果没有比该key的hash值大的，则从第一个node开始
            Integer i = virtualNodes.firstKey();
            //返回对应的服务器
            return virtualNodes.get(i);
        }else{
            //第一个Key就是顺时针过去离node最近的那个结点
            Integer i = sortedMap.firstKey();
            //返回对应的服务器
            return sortedMap.get(i);
        }
    }

    private static SortedMap<Integer,ServiceInstance> createNodes(List<ServiceInstance> instances,int virtualNodes){
        SortedMap<Integer,ServiceInstance> nodes = new TreeMap<>();
        for (ServiceInstance instance : instances) {
            String key = instance.getHost();
            for (int i = 0; i < virtualNodes; i++) {
                String virtualNodeName = "VN&&"+String.valueOf(i)+key;
                int hash = getHash(virtualNodeName);
                nodes.put(hash, instance);
            }
        }
        return nodes;
    }
}
