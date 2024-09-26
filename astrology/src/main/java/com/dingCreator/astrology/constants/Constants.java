package com.dingCreator.astrology.constants;

/**
 * @author ding
 * @date 2023/4/19
 */
public class Constants {
    // **----** 错误码前缀 **----**
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
     * 装备错误码前缀
     */
    public static final String EQUIPMENT_EXCEPTION_PREFIX = "E_EQUIPMENT_";
    /**
     * 任务错误码前缀
     */
    public static final String TASK_EXCEPTION_PREFIX = "E_TASK_";
    /**
     * 系统错误码前缀
     */
    public static final String SYS_EXCEPTION_PREFIX = "E_SYS_";
    /**
     * 世界boss错误码前缀
     */
    public static final String WORLD_BOSS_EXCEPTION_PREFIX = "E_WORLD_BOSS_";

    // **----** 经验相关配置 **----**
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
    /**
     * 讨伐世界boss CD
     */
    public static final int ATTACK_WORLD_BOSS_CD = 60;

    // **----** CD key前缀 **----**
    /**
     * 战斗CD前缀
     */
    public static final String CD_BATTLE_PREFIX = "cd_battle_";
    /**
     * 讨伐世界boss CD前缀
     */
    public static final String CD_WORLD_BOSS_PREFIX = "cd_world_boss_";

    // **----** 组队配置 **----**
    /**
     * 组队上限
     */
    public static final int TEAM_MEMBER_LIMIT = 3;


    // **----** 系统配置 **----**
    /**
     * 最小页码
     */
    public static final int MIN_PAGE_INDEX = 1;
    /**
     * 默认页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;
    /**
     * 最大页大小
     */
    public static final int MAX_PAGE_SIZE = 50;
    /**
     * 最小页大小
     */
    public static final int MIN_PAGE_SIZE = 1;
    /**
     * 最低概率
     */
    public static final float MIN_RATE = 0F;
    /**
     * 最高概率
     */
    public static final float MAX_RATE = 1F;
    /**
     * 均不可
     */
    public static final String NONE = "None";
    /**
     * 均可
     */
    public static final String ALL = "All";
    /**
     * 时间配置
     */
    public static final String DATE_FORMAT_1 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_2 = "yyyy/MM/dd";
    public static final String DATE_FORMAT_3 = "yyyy年MM月dd日";
    public static final String DATE_FORMAT_4 = "yyyy.MM.dd";
    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final int MAX_HOUR = 23;
    public static final int MAX_MINUTE = 59;
    public static final int MAX_SECOND = 59;

    public static final int MIN_HOUR = 0;
    public static final int MIN_MINUTE = 0;
    public static final int MIN_SECOND = 0;
    /**
     * 获取key锁超时时间默认时长
     */
    public static final long GET_LOCK_TIME_OUT = 5 * 1000L;
    /**
     * 锁前缀-玩家
     */
    public static final String PLAYER_LOCK_PREFIX = "LOCK_PLAYER_";
    /**
     * 锁前缀-任务进度
     */
    public static final String TASK_SCHEDULE_LOCK_PREFIX = "LOCK_TASK_SCHEDULE_";
    /**
     * 每日最多攻击世界boss次数
     */
    public static final int WORLD_BOSS_MAX_ATK_TIMES = 3;

    // **----** 符号 **----**
    /**
     * 冒号
     */
    public final static String COLON = ":";
    /**
     * 破折号
     */
    public final static String DASH = "-";
    /**
     * 双引号
     */
    public static final String QUOTE = "";
    /**
     * 逗号
     */
    public static final String COMMA = ",";
    /**
     * 下划线
     */
    public static final String UNDERLINE = "_";
    /**
     * #号
     */
    public static final String SHARP = "#";
    /**
     * 分号
     */
    public static final String SEMICOLON = ";";
    /**
     * ~号
     */
    public static final String TILDE = "~";
    /**
     * ···省略号
     */
    public static final String ELLIPSIS = " · · ·";
    /**
     * 星号
     */
    public static final String ASTERISK = "*";
    /**
     * 箭头
     */
    public static final String ARROW_1 = "=>";
    /**
     * 箭头
     */
    public static final String ARROW_2 = "->";
    /**
     * 空格
     */
    public static final String BLANK = " ";
    /**
     * 点
     */
    public static final String DOT = ".";
}
