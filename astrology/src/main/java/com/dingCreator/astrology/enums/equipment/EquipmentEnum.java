package com.dingCreator.astrology.enums.equipment;

import com.dingCreator.astrology.dto.equipment.EquipmentPropertiesDTO;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.template.ExtraBattleProcessTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/3/24
 */
@Getter
@AllArgsConstructor
public enum EquipmentEnum {
    /**
     * 装备
     */
    EQUIPMENT_1(1L, "破碎的剑柄",
            "垃圾箱里翻出来的剑柄，没有任何攻击能力。但是剑柄也不是一无是处，至少能为你做出逃跑的决定增加不少勇气",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 10L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, 10L)
            ),
            EquipmentRankEnum.SP, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_2(2L, "武士刀的断刃",
            "捡来的武士刀碎片，无法用来攻击，但可以贴在身上当防具使用",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 5L)),
            EquipmentRankEnum.SP, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_3(3L, "星术废纸",
            "垃圾典籍上撕下来的废纸，在如厕没有带纸的时候可以撕下来应急",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HP, 100L)),
            EquipmentRankEnum.SP, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_4(4L, "腐烂的桃木屑",
            "腐烂的桃木碎屑，打架的时候可以扬在对方脸上让他们看不清东西",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 20L)),
            EquipmentRankEnum.SP, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_5(5L, "生锈的枪膛",
            "破烂的枪膛，没什么用处，直接往对方脸上扔，砸一下也挺疼的",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 5L)),
            EquipmentRankEnum.SP, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_6(6L, "兽角",
            "不知道是何种兽角，似乎没有任何能量遗留在上面，本身也不算坚硬，但用来顶一下人还是可以的",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 6L)),
            EquipmentRankEnum.SP, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_7(7L, "大骨棒子",
            "野外随意捡来的还算完整的大骨棒子，砸人还挺顺手的",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 7L)),
            EquipmentRankEnum.SP, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_8(8L, "轰天雷",
            "只是一种名叫“轰天雷”的鞭炮罢了",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 10L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, -5L)
            ),
            EquipmentRankEnum.SP, EquipmentTypeEnum.WEAPON
    ),

    EQUIPMENT_100(100L, "生锈的铁剑",
            "已经生锈的铁剑，似乎还能用？",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 8L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 5L)
            ),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_101(101L, "生锈的武士刀",
            "一把生锈的武士刀，切菜应该没什么问题",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 14L)),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_102(102L, "腐朽的桃木棍",
            "老神棍骗术的精髓，不过已经腐朽不堪",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 6L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, 6L)
            ),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_103(103L, "蒙尘的星术秘典",
            "星术家族用来垫桌脚的书，上面有很多灰尘",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 16L)),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_104(104L, "会卡壳的格洛克手枪",
            "一把经常卡壳的手枪，但似乎曾经血洗过一个邪教？",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 10L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.PENETRATE, 0.1F)
            ),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_105(105L, "黑色石碑",
            "通体漆黑的石碑，在一处遗迹中获得。从其残存的字迹所交代的信息中可以了解到，在遥远的无尽海域中央有一处圣山名唤“太衍”，" +
                    "是古修真者们尊为神迹的圣地。作为武器来说，抡起来伤害可观，但十分费劲",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 15L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, -10L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, -10L)
            ),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_106(106L, "款式独特的玉佩",
            "土里偶然挖出来的，款式与现代的款式都不相同，上面也没有任何文字，无法辨别是何人遗失的。似乎也无法作为正常武器使用。",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 2L)),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.JEWELRY
    ),
    EQUIPMENT_107(107L, "锄头",
            "农夫种地的农具，也是农民起义常用武器之一",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 10L)),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_108(108L, "量产型乒乓球发射器",
            "古代产物，在一处废弃的厂房中找到了大量此武器，据说是用于发射一种叫“乒乓球”的子弹的机器，乒乓球的其他记载已失传，" +
                    "人们无从得知这是何种子弹。不过，此机器也可以用于发射一些现代的子弹，缺点是射速慢，人们推测可能是子弹不适配导致的",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 5L)),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.WEAPON, JobEnum.GUN.getJobCode()
    ),
    EQUIPMENT_109(109L, "量产型一阶剑符",
            "现今的修真界学习工业化技术的产物，一条流水线一分钟就能产出200张",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 20L)),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode()
    ),

    EQUIPMENT_200(200L, "精致的钢剑",
            "无名的锻造师新出炉的钢剑，但似乎是量产品",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 120L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 50L)
            ),
            EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_201(201L, "精良的武士刀",
            "商品展柜最耀眼的那把，但是中看不中用",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 180L)),
            EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_202(202L, "崭新的桃木棍",
            "江湖神棍新做的拐杖，逃跑时不用再担心断掉",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 80L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, 80L)
            ),
            EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_203(203L, "星术秘典",
            "垫桌角的书没有被拿来垫桌角之前的样子",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 160L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 50L)
            ),
            EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_204(204L, "崭新的格洛克手枪",
            "新出厂的格洛克手枪，没有什么血腥的背景，但至少不会卡壳",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 120L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.PENETRATE, 0.15F)
            ),
            EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_205(205L, "锋利的砍柴刀",
            "村民砍柴所用的刀，听说村民们曾经用这把刀赶跑了一个行骗的神棍",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 80L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 80L)
            ),
            EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_206(206L, "喋血的钢爪",
            "一把沾满罪恶的爪子，邪修曾用它虐杀了无数无辜的人",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 100L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.15F)
            ),
            EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_207(207L, "破损的大钟",
            "一处废墟中找到的破损大钟，上面可识别的字迹中记载着“（此处请根据故事背景，插入一些信息）”，其中还有一丝灵力残留，修真者可引导出力量",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 30L)),
            EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode()
    ),
//    EQUIPMENT_28(28L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_29(29L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_30(30L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_31(31L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_32(32L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_33(33L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_34(34L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_35(35L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_36(36L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_37(37L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_38(38L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_39(39L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_40(40L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_41(41L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_42(42L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_43(43L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_44(44L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_45(45L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_46(46L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_47(47L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_48(48L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_49(49L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_50(50L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_51(51L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_52(52L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_53(53L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_54(54L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),
//    EQUIPMENT_55(55L, "",
//            "",
//            EquipmentTypeEnum.WEAPON
//    ),

    ;
    /**
     * ID
     */
    private final Long id;
    /**
     * 名称
     */
    private final String name;
    /**
     * 限制等级
     */
    private final Integer limitLevel;
    /**
     * 描述
     */
    private final String desc;
    /**
     * 属性
     */
    private final List<EquipmentPropertiesDTO> prop;
    /**
     * 装备类型
     */
    private final EquipmentTypeEnum equipmentTypeEnum;
    /**
     * 装备级别
     */
    private final EquipmentRankEnum equipmentRankEnum;
    /**
     * 限制职业
     */
    private final List<String> limitJob;
    /**
     * 额外技能
     */
    private final ExtraBattleProcessTemplate extraBattleProcessTemplate;

    EquipmentEnum(Long id, String name, String desc, List<EquipmentPropertiesDTO> prop,
                  EquipmentRankEnum equipmentRankEnum, EquipmentTypeEnum equipmentTypeEnum) {
        this.id = id;
        this.name = name;
        this.limitLevel = equipmentRankEnum.getDefaultLimitLevel();
        this.desc = desc;
        this.prop = prop;
        this.equipmentRankEnum = equipmentRankEnum;
        this.equipmentTypeEnum = equipmentTypeEnum;
        this.limitJob = Collections.singletonList("All");
        this.extraBattleProcessTemplate = new ExtraBattleProcessTemplate() {
        };
    }

    EquipmentEnum(Long id, String name, String desc, List<EquipmentPropertiesDTO> prop,
                  EquipmentRankEnum equipmentRankEnum, EquipmentTypeEnum equipmentTypeEnum, String jobCode) {
        this.id = id;
        this.name = name;
        this.limitLevel = equipmentRankEnum.getDefaultLimitLevel();
        this.desc = desc;
        this.prop = prop;
        this.equipmentRankEnum = equipmentRankEnum;
        this.equipmentTypeEnum = equipmentTypeEnum;
        this.limitJob = Collections.singletonList(jobCode);
        this.extraBattleProcessTemplate = new ExtraBattleProcessTemplate() {
        };
    }

    EquipmentEnum(Long id, String name, String desc, List<EquipmentPropertiesDTO> prop,
                  EquipmentRankEnum equipmentRankEnum, EquipmentTypeEnum equipmentTypeEnum, List<String> jobCode) {
        this.id = id;
        this.name = name;
        this.limitLevel = equipmentRankEnum.getDefaultLimitLevel();
        this.desc = desc;
        this.prop = prop;
        this.equipmentRankEnum = equipmentRankEnum;
        this.equipmentTypeEnum = equipmentTypeEnum;
        this.limitJob = jobCode;
        this.extraBattleProcessTemplate = new ExtraBattleProcessTemplate() {
        };
    }

    EquipmentEnum(Long id, String name, String desc, List<EquipmentPropertiesDTO> prop,
                  EquipmentRankEnum equipmentRankEnum, EquipmentTypeEnum equipmentTypeEnum,
                  ExtraBattleProcessTemplate extraBattleProcessTemplate) {
        this.id = id;
        this.name = name;
        this.limitLevel = equipmentRankEnum.getDefaultLimitLevel();
        this.desc = desc;
        this.prop = prop;
        this.equipmentRankEnum = equipmentRankEnum;
        this.equipmentTypeEnum = equipmentTypeEnum;
        this.limitJob = Collections.singletonList("All");
        this.extraBattleProcessTemplate = extraBattleProcessTemplate;
    }

    EquipmentEnum(Long id, String name, String desc, EquipmentTypeEnum equipmentTypeEnum, String jobCode,
                  List<EquipmentPropertiesDTO> prop, EquipmentRankEnum equipmentRankEnum,
                  ExtraBattleProcessTemplate extraBattleProcessTemplate) {
        this.id = id;
        this.name = name;
        this.limitLevel = equipmentRankEnum.getDefaultLimitLevel();
        this.desc = desc;
        this.prop = prop;
        this.equipmentRankEnum = equipmentRankEnum;
        this.equipmentTypeEnum = equipmentTypeEnum;
        this.limitJob = Collections.singletonList(jobCode);
        this.extraBattleProcessTemplate = extraBattleProcessTemplate;
    }

    private static final Map<Long, EquipmentEnum> EQUIPMENT_ENUM_MAP;
    private static final Map<String, EquipmentEnum> EQUIPMENT_ENUM_NAME_MAP;

    public static EquipmentEnum getById(Long id) {
        return EQUIPMENT_ENUM_MAP.get(id);
    }

    public static EquipmentEnum getByName(String name) {
        return EQUIPMENT_ENUM_NAME_MAP.get(name);
    }

    static {
        EQUIPMENT_ENUM_MAP = Arrays.stream(EquipmentEnum.values()).collect(Collectors.toMap(
                EquipmentEnum::getId, Function.identity()));
        EQUIPMENT_ENUM_NAME_MAP = Arrays.stream(EquipmentEnum.values()).collect(Collectors.toMap(
                EquipmentEnum::getName, Function.identity()));
    }
}
