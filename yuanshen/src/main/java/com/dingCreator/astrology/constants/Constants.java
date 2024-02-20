package com.dingCreator.yuanshen.constants;

/**
 * @author ding
 * @date 2023/4/19
 */
public class Constants {
    /**
     * 组队错误码前缀
     */
    public static final String TEAM_EXCEPTION_PREFIX = "TEAM_";
    /**
     * 经验值错误码前缀
     */
    public static final String EXP_EXCEPTION_PREFIX = "EXP_";
    /**
     * 玩家错误码前缀
     */
    public static final String PLAYER_EXCEPTION_PREFIX = "PLAYER_";


    /**
     * 组队上限
     */
    public static final int TEAM_MEMBER_LIMIT = 3;


    /**
     * 时间格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    // 经验相关配置
    /**
     * 最低等级
     */
    public static final int MIN_LEVEL = 1;
    /**
     * 最高等级
     */
    public static final int MAX_LEVEL = 100;
    /**
     * 最低阶级
     */
    public static final int MIN_RANK = 1;
    /**
     * 最高阶级
     */
    public static final int MAX_RANK = 5;
    /**
     * 经验达到上限后可存储经验
     */
    public static final int MAX_TMP_LEVEL = 5;

}
