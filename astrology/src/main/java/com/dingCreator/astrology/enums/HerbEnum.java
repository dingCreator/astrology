package com.dingCreator.astrology.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2025/3/7
 */
@Getter
@AllArgsConstructor
public enum HerbEnum {
    HERB_1(1L, 1, "万灵草", 1, 0, 0, 0, 1, 0),
    HERB_2(2L, 1, "星·万化灵草", 1, 0, 0, 0, 2, 1),
    HERB_3(3L, 1, "万炎精", 0, 1, 0, 0, 1, 0),
    HERB_4(4L, 1, "星·万化炎灵精", 0, 1, 0, 0, 2, 1),
    HERB_5(5L, 1, "万凌髓液", 0, 0, 1, 0, 1, 0),
    HERB_6(6L, 1, "星·万化凌髓液", 0, 0, 1, 0, 2, 1),
    HERB_7(7L, 1, "万毒草", 0, 0, 0, 1, 0, 0),
    HERB_8(8L, 1, "星·万化灵毒草", 0, 0, 0, 1, 0, 1),
    HERB_9(9L, 1, "万清根", 0, 0, 0, -1, 0, 0),
    HERB_10(10L, 1, "星·万灵化清根", 0, 0, 0, -1, 0, 1),
    HERB_101(101L, 2, "复灵叶", 1, 1, 0, -1, 4, 0),
    HERB_102(102L, 2, "星·复灵晶化叶", 1, 1, 0, -1, 6, 2),
    HERB_103(103L, 2, "火焰菇", 0, 1, 1, 1, 4, 0),
    HERB_104(104L, 2, "星·天火灵芝", 0, 1, 1, 0, 6, 2),
    HERB_105(105L, 2, "黄泉果", 1, 0, 1, 1, 4, 0),
    HERB_106(106L, 2, "星·往生泉源", 1, 0, 1, 0, 6, 2),
    HERB_107(107L, 2, "伴生灵草", 1, 1, 1, 1, 6, 0),
    HERB_108(108L, 2, "星·比翼金銮草", 1, 1, 1, 0, 9, 2),
    HERB_109(109L, 2, "红火草", 1, 2, 0, 2, 6, 0),
    HERB_110(110L, 2, "星·幻火灵草", 1, 2, 0, 0, 9, 2),
    HERB_111(111L, 2, "地炎根", 0, 3, 0, 2, 6, 0),
    HERB_112(112L, 2, "星·地狱熔岩根", 0, 3, 0,0, 9, 2),
    HERB_113(113L, 2, "灵补花蕊", 2, 0, 1, -2, 6, 0),
    HERB_114(114L, 2, "星·幻灵花蕊", 2, 0, 1, -4, 9, 2),
    HERB_115(115L, 2, "暮旦花露", 0, 0, 3, -2, 6, 0),
    HERB_116(116L, 2, "星·朝暮淳滴", 0, 0, 3, -4, 9, 2),
    HERB_117(117L, 2, "玄冰玉髓", 1, 0, 4, 3, 10, 0),
    HERB_118(118L, 2, "星·太虚玄髓", 1, 0, 4, 0, 15, 2),
    HERB_119(119L, 2, "养魂木", 4, 1, 0, -1,10, 0),
    HERB_120(120L, 2, "星·滋神灵木", 4, 1, 0, -3, 15, 2),
    HERB_121(121L, 2, "归息司荣菊", 2, 2, 1, -1, 10, 0),
    HERB_122(122L, 2, "星·荣菊清液", 2, 2, 1, -3, 15, 2),
    HERB_123(123L, 2, "长白草", 0, 0, 0, -3, 0, 0),
    HERB_124(124L, 2, "星·长白祛毒草", 0, 0, 0, -6, 0, 2),
    HERB_201(201L, 3, "地黄参", 3, 3, 0, 3, 18, 0),
    HERB_202(202L, 3, "星·地黄龙参", 3, 3, 0, 0, 24, 3),
    HERB_203(203L, 3, "天南星", 0, 3, 3, 3, 18, 0),
    HERB_204(204L, 3, "星.天冬南星", 0, 3, 3, 0, 24, 3),
    HERB_205(205L, 3, "雪莲精", 3, 0, 3, 2, 18, 0),
    HERB_206(206L, 3, "星·雪莲霜粹", 3, 0, 3, 0, 24, 3),
    HERB_207(207L, 3, "赤血藤", 4, 2, 0, 3, 18, 0),
    HERB_208(208L, 3, "星·碧血焕青藤", 4, 2, 0, 0, 24, 3),
    HERB_209(209L, 3, "灵斛", 2, 3, 2, -3, 21, 0),
    HERB_210(210L, 3, "星·轩辕灵生斛", 2, 3, 2, -8, 28, 3),
    HERB_211(211L, 3, "金血草", 3, 4, 1, 1, 24, 0),
    HERB_212(212L, 3, "星·金鸿沥血草", 3, 4, 1, 0, 32, 3),
    HERB_213(213L, 3, "焕生泉水", 0, 0, 0, -4, 0, 3),
    HERB_214(214L, 3, "星·落骨化生泉", 0, 0, 0, -10, 0, 3),
    HERB_215(215L, 3, "青冥果", 2, 5, 1, 3, 24, 0),
    HERB_216(216L, 3, "星·青冥皇灵果", 2, 5, 1, 0, 32, 3),
    HERB_217(217L, 3, "玉灵芝", 3, 5, 1, 3, 27, 0),
    HERB_218(218L, 3, "星·玉蓬玄晶芝", 3, 5, 1, 0, 36, 3),
    HERB_301(301L, 4, "紫星兰", 3, 3, 4, 4, 40, 1),
    HERB_302(302L, 4, "星·紫云皓星兰", 3, 3, 4, 0, 50, 5),
    HERB_303(303L, 4, "金兰仙果", 5, 8, 2, 4, 60, 1),
    HERB_304(304L, 4, "星·金灵元核", 5, 8, 2, 0, 75, 5),
    HERB_401(401L, 5, "太初元露", 7, 2, 7, 5, 80, 2),
    HERB_402(402L, 5, "星·天泽露华", 7, 2, 7, 0, 96, 10),
    HERB_403(403L, 5, "混沌龙血", 8, 8, 3, 5, 98, 2),
    HERB_404(404L, 5, "星·天龙金皇血", 8, 8, 3, 0, 117, 10),
    HERB_405(405L, 5, "七宝妙砂", 0, 0, 0, -6, 0, 2),
    HERB_406(406L, 5, "星·净世砂金", 0, 0, 0, -20, 0, 10),
    HERB_1001(1001L, 99, "道品青莲", 215, 125, 210, 9, 5500, 10),
    HERB_1002(1002L, 99, "星·九十九品创世青莲", 215, 125, 210, 0, 550000, 100),
    ;
    /**
     * id
     */
    private final Long id;
    /**
     * 等级
     */
    private final Integer rank;
    /**
     * 名称
     */
    private final String name;
    /**
     * 生机
     */
    private final Integer vigor;
    /**
     * 温热
     */
    private final Integer warn;
    /**
     * 寒韵
     */
    private final Integer cold;
    /**
     * 毒性
     */
    private final Integer toxicity;
    /**
     * 品质
     */
    private final Integer quality;
    /**
     * 星辰之力
     */
    private final Integer star;

    private static final Map<Long, HerbEnum> HERB_ID_MAP;
    private static final Map<String, HerbEnum> HERB_NAME_MAP;

    static {
        HERB_ID_MAP = Arrays.stream(HerbEnum.values()).collect(Collectors.toMap(HerbEnum::getId, Function.identity()));
        HERB_NAME_MAP = Arrays.stream(HerbEnum.values()).collect(Collectors.toMap(HerbEnum::getName, Function.identity()));
    }

    public static HerbEnum getByName(String herbName) {
        return HERB_NAME_MAP.get(herbName);
    }

    public static HerbEnum getById(Long id) {
        return HERB_ID_MAP.get(id);
    }
}
