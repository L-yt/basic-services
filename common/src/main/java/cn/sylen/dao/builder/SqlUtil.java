package cn.sylen.dao.builder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sylen.common.exception.CoreException;
import cn.sylen.common.util.Entry;
import cn.sylen.common.util.StringUtil;

public class SqlUtil {
    private final static int TYPE_SPLITER = 1;
    private final static int TYPE_NORMAL = 2;
    private final static int TYPE_SPACE = 3;

    private final static int TYPE_PARAM = 4;
    private final static int TYPE_PARAM_START = 5;
    private final static int TYPE_PARAM_END = 6;

    private static int[] matchChars = new int[255];
    private static Set<String> spliters = new HashSet<String>();
    static {
        for(int i=0; i<matchChars.length; i++) {
            matchChars[i] = 0;
        }

        matchChars['!'] = TYPE_SPLITER;
        matchChars['='] = TYPE_SPLITER;
        matchChars['>'] = TYPE_SPLITER;
        matchChars['<'] = TYPE_SPLITER;
        matchChars[' '] = TYPE_SPACE;

        matchChars[')'] = TYPE_NORMAL;
        //		matchChars['\''] = TYPE_NORMAL;
        matchChars['('] = TYPE_NORMAL;

        matchChars['#'] = TYPE_PARAM;
        matchChars['$'] = TYPE_PARAM;

        matchChars['{'] = TYPE_PARAM_START;
        matchChars['}'] = TYPE_PARAM_END;

        spliters.add("NOT");
        spliters.add("LIKE");
        spliters.add("IS");
        spliters.add("IN");
    }

    public static String convertToCountSql(String sql) {
        if(StringUtil.isEmpty(sql)) {
            return sql;
        }

        int fromIdx = sql.indexOf(" from ");
        if(fromIdx == -1) {
            fromIdx = sql.indexOf(" FROM ");
        }

        if(fromIdx == -1) {
            throw new CoreException("sql " + sql + " 没有包含from字样");
        }

        int endIdx = sql.indexOf(" order by");

        if(endIdx == -1) {
            endIdx = sql.indexOf(" limit ");
            if(endIdx == -1) {
                endIdx = sql.indexOf(" ORDER BY");
            }
            if(endIdx == -1) {
                endIdx = sql.length();
            }
        }

        StringBuffer countSql = new StringBuffer();
        countSql.append("select count(1) as count from ")
        .append(sql.substring(fromIdx + 6, endIdx));

        return countSql.toString();
    }

    /**
     * 把sql normalize成可以替换的列表
     * 如id='id1' and first_register_date < '2013' and status like '%abc' and status not like '%cde' and status is null,
     * 最后会返回成list,包含几个字符串:"%s = 'id1' and %s < '2013' and %s like '%abc' and %s not like '%cde' and %s is null",
     * "id", "first_register_date", "status", "status", "status"
     * @param sql
     * @return
     */
    public static List<String> generateSqlParams(String sql) {
        List<String> ret = new ArrayList<String>();
        StringBuffer sqlsb = new StringBuffer();
        StringBuffer tmpsb = new StringBuffer();
        boolean spaceMatch = false;
        for(int i=0; i<sql.length(); i++) {
            char c = sql.charAt(i);
            if(c >= matchChars.length) {
                tmpsb.append(c);
                continue;
            }

            int type = matchChars[c];

            if(type == TYPE_SPLITER
                    && !(c == '>' && (sql.charAt(i-1) == '>'  || sql.charAt(i+1) == '>'))
                    // 不是>>的字符
                    ) {
                if(tmpsb.length() > 0) {
                    sqlsb.append("%s ");
                    ret.add(tmpsb.toString());
                    tmpsb = new StringBuffer();
                    spaceMatch = false;
                }
                sqlsb.append(c);
            } else if(type == TYPE_SPACE) {
                spaceMatch = true;
                String nextStr = nextSpaceStr(sql.substring(i + 1));
                if(StringUtil.isNotEmpty(nextStr)) {
                    if(spliters.contains(nextStr.toUpperCase())) {
                        if(tmpsb.length() > 0) {
                            sqlsb.append("%s ").append(nextStr);
                            ret.add(tmpsb.toString());
                            tmpsb = new StringBuffer();
                            spaceMatch = false;
                            i += nextStr.length();
                        }
                    }
                }
            } else if(type == TYPE_NORMAL) {
                if(tmpsb.length() > 0) {
                    sqlsb.append(tmpsb.toString());
                    tmpsb = new StringBuffer();
                }
                if(spaceMatch) {
                    sqlsb.append(" ");
                    spaceMatch = false;
                }
                sqlsb.append(c);
            } else {
                if(spaceMatch) {
                    sqlsb.append(tmpsb.toString());
                    sqlsb.append(" ");
                    spaceMatch = false;
                    tmpsb = new StringBuffer();
                }
                tmpsb.append(c);
            }
        }

        if(tmpsb.length() > 0) {
            sqlsb.append(tmpsb.toString());
        }

        ret.add(0, sqlsb.toString());
        return ret;
    }

    protected static boolean isSpliterChar(char c) {
        if(c >= matchChars.length) {
            return false;
        }

        return matchChars[c] == TYPE_SPLITER;
    }

    /**
     * 返回当前字符到下一个空格为止的字符串
     * @param str
     * @return
     */
    public static String nextSpaceStr(String str) {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if(c == ' ') {
                return sb.toString();
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 获取sql中的参数名,同时把sql中的参数进行normalize
     * 如id=#{ID} and first_register_date >= ${date},
     * 最后会返回成list,包含三个字符串:"id=#{ID_q} and first_register_date >= ${date_q}", "ID"和"date"
     * @param sql
     * @return
     */
    public static List<String> getSqlParamNames(String sql) {
        List<String> ret = new ArrayList<String>();

        StringBuffer sqlsb = new StringBuffer();
        StringBuffer tmpsb = new StringBuffer();
        boolean paramMatch = false;
        boolean paramStarted = false;
        for(int i=0; i<sql.length(); i++) {
            char c = sql.charAt(i);
            if(c >= matchChars.length) {
                sqlsb.append(c);
                continue;
            }

            int type = matchChars[c];

            if(!paramMatch) {
                sqlsb.append(c);

                // 还没有匹配
                if(type == TYPE_PARAM) {
                    paramMatch = true;
                    paramStarted = false;
                    continue;
                }
            } else {
                if(paramStarted) {
                    if(type == TYPE_PARAM_END) {
                        if(tmpsb.length() > 0) {
                            if(tmpsb.charAt(0) != '_') {
                                tmpsb.insert(0, "_");
                            }
                            ret.add(tmpsb.toString());
                            sqlsb.append(tmpsb.toString());
                            tmpsb = new StringBuffer();
                        }
                        paramMatch = false;
                        paramStarted = false;
                        sqlsb.append(c);
                        continue;
                    } else {
                        tmpsb.append(c);
                    }
                } else {
                    if(type != TYPE_PARAM_START) {
                        paramMatch = false;
                    } else {
                        paramStarted = true;
                    }
                    sqlsb.append(c);
                }
            }

        }
        if(tmpsb.length() > 0) {
            sqlsb.append(tmpsb.toString());
        }

        ret.add(0, sqlsb.toString());
        return ret;
    }

    /**
     * 把类似于table.column,切成表和字段
     */
    public static Entry<String, String> splitTableColumn(String column) {
        if(column == null) {
            return null;
        }

        String[] array = column.split("\\.", 2);
        if(array.length == 2) {
            return new Entry<String, String>(array[0], array[1]);
        } else {
            return new Entry<String, String>(null, column);
        }

    }

    /**
     * 生成in参数查询语句，如in #{statuses}，会转换成为产生in ('a', 'b', 'c')
     */
    public static String genInParams(String query, Map<String, Object> parameterMap) {
        while (query != null && query.indexOf("in #") != -1) {

            int startIdx = query.indexOf("in #");
            int firstIdx = query.indexOf("{", startIdx);
            int endIdx = query.indexOf("}", startIdx);

            String paramKey = query.substring(firstIdx + 1, endIdx);
            String startQuery = query.substring(0, startIdx + 3);

            Object obj = parameterMap.get(paramKey);
            StringBuffer sb = new StringBuffer();
            if(obj != null) {
                sb.append("(");
                if(obj instanceof List) {
                    List<?> l = (List<?>) obj;
                    for(Object o : l) {
                        if(sb.length() > 1) {
                            sb.append(',');
                        }
                        sb.append(getItemStr(o));
                    }
                } else if(obj.getClass().isArray()) {
                    int len = Array.getLength(obj);
                    for(int i=0; i<len; i++) {
                        Object o = Array.get(obj, 1);
                        if(sb.length() > 1) {
                            sb.append(',');
                        }
                        sb.append(getItemStr(o));
                    }

                } else {
                    sb.append(getItemStr(obj));
                }
                sb.append(')');
            }

            String endQuery = query.substring(endIdx + 1, query.length());

            query = startQuery + sb.toString() + endQuery;
        }


        return query;
    }

    protected static String getItemStr(Object obj) {
        if(obj instanceof String) {
            return "'" + obj + "'";
        } else {
            return String.valueOf(obj);
        }
    }
}
