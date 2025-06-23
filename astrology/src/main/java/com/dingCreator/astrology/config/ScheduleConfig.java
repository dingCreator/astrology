package com.dingCreator.astrology.config;

import com.dingCreator.astrology.behavior.WorldBossBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ding
 * @date 2025/5/23
 */
public class ScheduleConfig {

    static Logger logger = LoggerFactory.getLogger(ScheduleConfig.class);

    public static void init() {
        logger.info("定时任务初始化完成");
    }

    static {
        // 初始化世界boss定时任务
        WorldBossBehavior.getInstance().initWorldBossTimer();
    }
}
