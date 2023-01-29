package com.cheese.shiro.common;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * context 编码/解码类
 *
 * @author sobann
 */
public interface Coder {
    /**
     * 将string 返序列化为 context对象
     *
     * @param context
     * @return
     */
    Object decode(String context);

    /**
     * 将传递上下文 序列化为 string
     *
     * @param context
     * @return
     * @throws JsonProcessingException
     * @throws Exception
     */
    String encode(Object context);
}
