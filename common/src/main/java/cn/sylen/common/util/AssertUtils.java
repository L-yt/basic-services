package cn.sylen.common.util;

import cn.sylen.common.exception.CoreException;

/**
 * 断言帮助类
 */
public class AssertUtils {
    public static void assertNotNull(Object o, String message) {
        if (o == null) {
            throw new CoreException(message);
        }
    }
}
