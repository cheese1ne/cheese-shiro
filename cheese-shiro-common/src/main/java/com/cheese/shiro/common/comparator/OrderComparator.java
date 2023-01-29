package com.cheese.shiro.common.comparator;

import com.cheese.shiro.common.Order;

import java.util.Comparator;

/**
 * Order处理顺序的比较器
 *
 * @author sobann
 */
public class OrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order o1, Order o2) {
        return o1.order() - o2.order();
    }
}
