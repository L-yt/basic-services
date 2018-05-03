package cn.sylen.dao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * primary key声明
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlPrimaryKey {
	String value() default "";
}
