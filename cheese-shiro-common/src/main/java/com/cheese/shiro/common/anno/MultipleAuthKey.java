package com.cheese.shiro.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 复合注解，对Dao层的@AuthKey和@AuthMapKey进行管理
 *
 * @author sobann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MultipleAuthKey {
    AuthKey[] authKeys() default {};

    AuthMapKey[] authMapKeys() default {};
}
