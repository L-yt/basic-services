package cn.sylen.common.util;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;

public class EncryptUtil {

    public static String openLdapEncoding(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(str.getBytes());
            byte[] b = md.digest();
            byte[] base64Encrypted = Base64.encodeBase64(b);
            return "{SHA}"+ new String(base64Encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String dbEncoding(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(str.getBytes());
            byte[] b = md.digest();
            byte[] base64Encrypted = Base64.encodeBase64(b);
            return new String(base64Encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
