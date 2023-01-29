package com.cheese.shiro.common.util;


import org.springframework.util.DigestUtils;

/**
 * md5工具
 *
 * @author sobann
 */
public class MD5Utils {

    private static final int MD5_LENGTH = 32;

    /**
     * md5加密后的数据原样返回，未加密的数据进行md5加密
     *
     * @param value
     * @return
     */
    public static String doPrimaryStr(String value) {
        if (value.length() != MD5_LENGTH) {
            value = DigestUtils.md5DigestAsHex(value.getBytes());
        }
        return value.toLowerCase();
    }
}
