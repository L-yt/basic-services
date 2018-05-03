package cn.sylen.dao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 定义对一个字段的sql修饰
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlField {
	/**
	 * 数据库中对应的字段名
	 */
	String value() default "";

	Class<?> clazz() default Object.class;

	/**
	 * 字段的默认值，在数据库记录进行insert时生效
	 */
	String defaultValue() default "";

	/**
	 * 字段更新时，该字段必须要填的值
	 */
	String updateValue() default "";
	/**
	 * 是否做为表的查询字段，为false时不做为查询字段
	 */
	boolean queryKey() default false;

	/**
	 * 是否做为唯一键，默认情况为false。唯一键是在查询及更新时做查询条件
	 */
	boolean uniqKey() default false;
	/**
	 * 是否做为主键，默认情况为false。
	 */
	boolean privateKey() default false;
}
