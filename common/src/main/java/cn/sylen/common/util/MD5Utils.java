package cn.sylen.common.util;

import java.security.MessageDigest;

/**
 * md5算法
 * 
 * @author alex
 * 
 */
public class MD5Utils {

    public static String md5(String input) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte b[] = md.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    sb.append("0");
                sb.append(Integer.toHexString(i));
            }
        } catch (Exception e) {
            return null;
        }
        return sb.toString();
   }
    
   public static void main(String[] args){
       
   }
}
