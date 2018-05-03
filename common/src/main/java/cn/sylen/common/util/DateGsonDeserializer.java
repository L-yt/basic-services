package cn.sylen.common.util;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSONException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@SuppressWarnings("rawtypes")
public class DateGsonDeserializer implements JsonDeserializer{

    @Override
    public Object deserialize(JsonElement json, Type clazz, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null){
            return null;
        }
        String val = json.getAsString();
        String strVal = (String) val;
        if (strVal.length() == 0) {
            return null;
        }
        
        if(strVal.length() <= 14 && RegexUtil.isDigit(strVal)){
            try {
                Long longVal = Long.parseLong(strVal);
                if(clazz == java.util.Date.class){
                    return new java.util.Date(longVal);
                }
                if(clazz == java.sql.Date.class){
                    return new java.sql.Date(longVal);
                }
                if(clazz == java.sql.Timestamp.class){
                    return new java.sql.Timestamp(longVal);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        java.util.Date date = DateTimeUtil.parseStringToDate((String)val);
        if(clazz == java.util.Date.class){
            return date;
        }
        if(clazz == java.sql.Date.class){
            return new java.sql.Date(date.getTime());
        }
        if(clazz == java.sql.Timestamp.class){
            return new java.sql.Timestamp(date.getTime());
        }
        throw new JSONException("parse error where val="+val);
    }

}
