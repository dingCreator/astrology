package com.dingCreator.astrology.constants;

/**
 * @author ding
 * @date 2023/4/19
 */
public class Constants {
    /**
     * 组队错误码前缀
     */
    public static final String TEAM_EXCEPTION_PREFIX = "E_TEAM_";
    /**
     * 经验值错误码前缀
     */
    public static final String EXP_EXCEPTION_PREFIX = "E_EXP_";
    /**
     * 玩家错误码前缀
     */
    public static final String PLAYER_EXCEPTION_PREFIX = "E_PLAYER_";
    /**
     * CD错误码前缀
     */
    public static final String CD_EXCEPTION_PREFIX = "E_CD_";
    /**
     * 阶级错误码前缀
     */
    public static final String RANK_EXCEPTION_PREFIX = "E_RANK_";
    /**
     * 怪物错误码前缀
     */
    public static final String MONSTER_EXCEPTION_PREFIX = "E_MONSTER_";
    /**
     * 战斗错误码前缀
     */
    public static final String BATTLE_EXCEPTION_PREFIX = "E_BATTLE_";
    /**
     * 技能错误码前缀
     */
    public static final String SKILL_EXCEPTION_PREFIX = "E_SKILL_";
    /**
     * 技能错误码前缀
     */
    public static final String MAP_EXCEPTION_PREFIX = "E_MAP_";
    /**
     * 技能错误码前缀
     */
    public static final String DUNGEON_EXCEPTION_PREFIX = "E_DUNGEON_";

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
     * 最长挂机时间
     */
    public static final long MAX_HANG_UP_TIME = 24 * 60;
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

    // 战斗相关配置
    /**
     * 接受决斗超时时间
     */
    public static final int ACCEPT_BATTLE_TIME_OUT = 30;
    /**
     * 发起决斗冷却时间
     */
    public static final int BATTLE_CD = 30;

    // CD key前缀
    /**
     * 战斗CD前缀
     */
    public static final String CD_BATTLE_PREFIX = "cd_battle_";
}
