package cn.sylen.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * 记录消耗的时间
 * @author wenming.hong
 * @since 2012-8-27
 */
public class TimeProfile {
    private final static Logger logger = LoggerFactory.getLogger(TimeProfile.class);

    /**
     * 启动时间
     */
    private long startTime;

    private long lastMarkTime;

    /**
     * 记录所有打标记的时间点
     */
    private List<Entry<String, Long>> timeTags;

    public TimeProfile() {
        startTime = System.currentTimeMillis();
        lastMarkTime = System.currentTimeMillis();
        timeTags = new ArrayList<Entry<String, Long>>();
    }

    public void markTime() {
        lastMarkTime = System.currentTimeMillis();
    }

    /**
     * 打时间标记
     */
    public void addTimeTag(String tag) {
        if(timeTags == null) {
            timeTags = new ArrayList<Entry<String, Long>>();
        }

        timeTags.add(new Entry<String, Long>(tag, System.currentTimeMillis() - lastMarkTime));
        markTime();
    }

    /**
     * 打时间标记
     */
    public void addTimeTag(String tag, long eclapse) {
        if(timeTags == null) {
            timeTags = new ArrayList<Entry<String, Long>>();
        }

        timeTags.add(new Entry<String, Long>(tag, eclapse));
    }

    /**
     * 得到消耗的时间
     */
    public long getEclapseTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 得到所有打标记是消耗的时间
     */
    public List<Entry<String, Long>> getMarkEclapseTimes() {
        return timeTags;
    }

    public void printTimeLog() {
        logger.info("it costs " + getEclapseTime() + "ms from start");
    }
}
