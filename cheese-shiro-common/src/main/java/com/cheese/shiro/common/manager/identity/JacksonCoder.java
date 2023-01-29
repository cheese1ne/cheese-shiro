package com.cheese.shiro.common.manager.identity;

import com.cheese.shiro.common.Coder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;

/**
 * 默认编码解码类
 * 须保证各组件中 存在 对应 context class（反序列化使用）
 *
 * @author sobann
 */
public class JacksonCoder implements Coder {

    private ObjectMapper objectMapper;
    private JavaType javaType;

    public JacksonCoder() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        this.objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        this.javaType = TypeFactory.defaultInstance().constructType(Object.class);
    }


    @Override
    public Object decode(String context) {
        try {
            return objectMapper.readValue(context, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encode(Object context) {
        try {
            return objectMapper.writeValueAsString(context);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
