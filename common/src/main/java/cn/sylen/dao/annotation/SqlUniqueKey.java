package cn.sylen.dao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * unique key声明
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlUniqueKey {
	String value() default "";
}
