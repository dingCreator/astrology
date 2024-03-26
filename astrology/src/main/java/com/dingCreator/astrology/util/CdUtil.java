package com.dingCreator.astrology.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ding
 * @date 2024/3/24
 */
public class CdUtil {
    /**
     * cd
     */
    private static final Map<String, LocalDateTime> CD_MAP = new ConcurrentHashMap<>();

    /**
     * 获取CD剩余时间
     *
     * @param key key
     * @return 剩余CD秒数，若 < 0表明不在CD中
     */
    public static long getCd(String key) {
        LocalDateTime localDateTime = CD_MAP.get(key);
        if (Objects.isNull(localDateTime)) {
            return -1L;
        }
        Duration duration = Duration.between(LocalDateTime.now(), localDateTime);
        return duration.getSeconds();
    }

    /**
     * 增加CD
     */
    public static void addCd(String key, long seconds) {
        CD_MAP.put(key, LocalDateTime.now().plusSeconds(seconds));
    }
}
