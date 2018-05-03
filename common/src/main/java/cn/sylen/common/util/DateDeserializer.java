package cn.sylen.common.util;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.AbstractDateDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

public class DateDeserializer extends AbstractDateDeserializer implements ObjectDeserializer{

    public final static DateDeserializer instance = new DateDeserializer();
    
    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {
        if (val == null){
            return null;
        }
        if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.length() == 0) {
                return null;
            }
            
            if(strVal.length() <= 14 && RegexUtil.isDigit(strVal)){
                try {
                    Long longVal = Long.parseLong(strVal);
                    if(clazz == java.util.Date.class){
                        return (T) new java.util.Date(longVal);
                    }
                    if(clazz == java.sql.Date.class){
                        return (T) new java.sql.Date(longVal);
                    }
                    if(clazz == java.sql.Timestamp.class){
                        return (T) new java.sql.Timestamp(longVal);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            
            java.util.Date date = DateTimeUtil.parseStringToDate((String)val);
            if(clazz == java.util.Date.class){
                return (T) date;
            }
            if(clazz == java.sql.Date.class){
                return (T) new java.sql.Date(date.getTime());
            }
            if(clazz == java.sql.Timestamp.class){
                return (T) new java.sql.Timestamp(date.getTime());
            }
        }
        if (val instanceof Long){
            Long longVal = (Long) val;
            if(clazz == java.util.Date.class){
                return (T) new java.util.Date(longVal);
            }
            if(clazz == java.sql.Date.class){
                return (T) new java.sql.Date(longVal);
            }
            if(clazz == java.sql.Timestamp.class){
                return (T) new java.sql.Timestamp(longVal);
            }
        }
        throw new JSONException("parse field "+fieldName+" error where val="+val);
    }

}
