package com.dingCreator.astrology.enums.equipment;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.BattleBuffDTO;
import com.dingCreator.astrology.dto.BattleDTO;
import com.dingCreator.astrology.dto.BuffDTO;
import com.dingCreator.astrology.dto.equipment.EquipmentPropertiesDTO;
import com.dingCreator.astrology.dto.organism.OrganismDTO;
import com.dingCreator.astrology.dto.skill.SkillEffectDTO;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import com.dingCreator.astrology.enums.OrganismPropertiesEnum;
import com.dingCreator.astrology.enums.exception.EquipmentExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.skill.DamageTypeEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.util.template.ExtraBattleProcessTemplate;
import com.dingCreator.astrology.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
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
    // EQUIPMENT_9,开启某秘境的钥匙
    EQUIPMENT_9(9L, "纸糊的衣服",
            "纸糊成的衣服，上面画着一些修真界无法解析的鬼画符",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 1L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 2L)
            ),
            EquipmentRankEnum.SP, EquipmentTypeEnum.ARMOR
    ),
    EQUIPMENT_10(10L, "漏风的棉袄",
            "心里哇凉哇凉的",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 2L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 2L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, -2L)
            ),
            EquipmentRankEnum.SP, EquipmentTypeEnum.ARMOR
    ),
    EQUIPMENT_11(11L, "铁桶",
            "游戏《法器大战邪修》爆火后，一些人参照游戏中某位邪修的造型，将铁桶戴在头上，意外发现防御效果不错",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 3L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 3L)
            ),
            EquipmentRankEnum.SP, EquipmentTypeEnum.ARMOR
    ),
    EQUIPMENT_12(12L, "硬纸板",
            "趁废品店老板不注意顺走的硬纸板，上面有修真者随意画上的加固符文，使得此硬纸板的硬度堪比石头",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 3L)
            ),
            EquipmentRankEnum.SP, EquipmentTypeEnum.ARMOR
    ),
    EQUIPMENT_13(13L, "门板",
            "邪修洗劫完某家后，顺手拆下的门，可以当做盾牌使用",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 2L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 1L)
            ),
            EquipmentRankEnum.SP, EquipmentTypeEnum.ARMOR
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
    EQUIPMENT_110(110L, "屠刀",
            "屠夫用于宰杀牲畜的刀",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 15L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.PENETRATE, 0.01F)
            ),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.WEAPON
    ),
    EQUIPMENT_111(111L, "量产型一阶甲符",
            "现今的修真界学习工业化技术的产物，一条流水线一分钟就能产出200张。",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 15L)
            ),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.ARMOR, JobEnum.XIU_ZHEN.getJobCode()
    ),
    EQUIPMENT_112(112L, "粗布衣",
            "非常粗厚的布衣，能起到一定的防护作用，平民过冬的选择之一",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 10L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 10L)
            ),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.ARMOR
    ),
    EQUIPMENT_113(113L, "橄榄球头盔",
            "游戏《法器大战邪修》爆火后，一些人参照游戏中某位邪修的造型，将橄榄球头盔戴在头上，意外发现防御效果不错",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 20L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 20L)
            ),
            EquipmentRankEnum.ORDINARY, EquipmentTypeEnum.ARMOR
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
    EQUIPMENT_208(208L, "量产型8086芯片",
            "某处遗迹中找到的唯一还能用的一块芯片，可以用于计算对方的移动轨迹，增加一定的命中率，一起找到的还有某块储存设备，读取到了以下内容：" +
                    "“（此处请根据故事背景，插入一些信息）”",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 100L)),
            EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON, JobEnum.GUN.getJobCode()
    ),
    EQUIPMENT_209(209L, "量产型AK",
            "上古年代的产物，居然还能使用，其中藏着一张纸，上面写着“（此处请根据故事背景，插入一些信息）”，字迹越到后面越无法辨认，" +
                    "像是上一任主人临终前写下的",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 120L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.PENETRATE, 0.2F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, -100L)
            ), EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON, JobEnum.GUN.getJobCode()
    ),
    EQUIPMENT_210(210L, "染血的圣典",
            "一具枯骨旁边找到的圣典，血迹掩盖了大部分的内容，因此能发挥的作用并不稳定，能辨别的部分，记载着“（此处请根据故事背景，插入一些信息）”",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 200L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, -50L)
            ), EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON, JobEnum.MAGICIAN.getJobCode()
    ),
    EQUIPMENT_211(211L, "量产型二阶剑符",
            "现今的修真界学习工业化技术的产物，一条流水线一分钟能产出20张",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 200L)),
            EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode()
    ),
    EQUIPMENT_212(212L, "量产型二阶甲符",
            "现今的修真界学习工业化技术的产物，一条流水线一分钟能产出20张",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 150L)),
            EquipmentRankEnum.NORMAL, EquipmentTypeEnum.ARMOR, JobEnum.XIU_ZHEN.getJobCode()
    ),
    EQUIPMENT_213(213L, "岩制铠甲",
            "岩石经修真者法术处理后制成的铠甲，防御力惊人，但是非常沉重",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 300L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 100L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, -100L)
            ), EquipmentRankEnum.NORMAL, EquipmentTypeEnum.ARMOR
    ),


    EQUIPMENT_300(300L, "青苍剑",
            "修仙王朝初级灵剑中的一种，由寒铁精所铸十分轻便，能有效提升使用者的灵活性，多用作飞剑使用",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 500L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 120L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode()
    ),
    EQUIPMENT_301(301L, "地炎剑",
            "修仙王朝初级灵剑中的一种，因由地炎淬炼而得名，实战中有几率爆发地炎的力量，受到许多修真者的钟爱",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.2F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode()
    ),
    EQUIPMENT_302(302L, "真武剑",
            "修仙王朝初级灵剑中的一种，厚重而又锋利，经历过修真王朝至宝真武之石的照临，使其即使是用剑面拍人也能造成不小的伤害",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 800L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.1F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode()
    ),
    EQUIPMENT_303(303L, "磐岩大剑",
            "攻守兼备的磐岩大剑，为雇佣兵们所喜爱。因为这把剑超凡的防御性，能给游走在刀尖上的他们带来非凡的安全感",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 400L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 300L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 100L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode()
    ),
    EQUIPMENT_304(304L, "铁蜂刺",
            "修真王朝传奇匠人 千秋和 的处女作，后被大量仿制投入使用，其剑锋如蜂刺一般，能够轻松刺穿敌人的护甲",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.PENETRATE, 0.25F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode()
    ),
    EQUIPMENT_305(305L, "噬魂剑",
            "邪修运用邪修功法打造的剑，能够吞噬人的灵魂以强化自身",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.LIFE_STEALING, 0.12F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode()
    ),
    EQUIPMENT_306(306L, "薙刀",
            "不知名的薙刀，是已经覆灭的蔽日岛中军人装备的一种。偶尔会有从死烬之渊走出来的亡命徒拿着它到市场上售卖",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 900L)),
            EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.SI_DI_WU_SHI.getJobCode()
    ),
    EQUIPMENT_307(307L, "太刀",
            "不知名的太刀 ，是已经覆灭的蔽日岛中军人装备的一种。即使埋没于死烬之渊许久，其锋利程度也没有丝毫减弱",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 700L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.1F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.SI_DI_WU_SHI.getJobCode()
    ),
    EQUIPMENT_308(308L, "打刀",
            "不知名的打刀，是已经覆灭的蔽日岛军人装备的一种。相比其他刀型能更加快速的拔刀，适合以速制胜的武士",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 100L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.SI_DI_WU_SHI.getJobCode()
    ),
    EQUIPMENT_309(309L, "斥日刀",
            "厚重而锋利的大刀，因其能够轻易砍碎蔽日岛重甲武士的铠甲而得名，曾大量应用于战场，蔽日岛覆灭后逐渐减少了使用",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.PENETRATE, 0.2F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.SI_DI_WU_SHI.getJobCode()
    ),
    EQUIPMENT_310(310L, "影栾刃",
            "已经覆灭的蔽日岛中杀手组织“影天阁”普通成员配备的一般装备，蔽日岛覆灭后，影天阁也名存实亡，一些成员逃出组织后将装备拿出售卖",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, 120L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.SI_DI_WU_SHI.getJobCode()
    ),
    EQUIPMENT_311(311L, "阵刀组",
            "用灵阵封存的普通灵刀组，相比于攻击性更注重防御性，刀阵展开之后可攻可守十分难缠",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 300L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 400L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 100L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.SI_DI_WU_SHI.getJobCode()
    ),
    EQUIPMENT_312(312L, "火焰秘典",
            "圣城普通星术典籍之一，能够在星术中融合火焰之力，对敌方造成沉痛的打击",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 800L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.15F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.MAGICIAN.getJobCode()
    ),
    EQUIPMENT_313(313L, "疾风秘典",
            "圣城普通星术典籍之一，能够转化出疾风之力，使使用者在战斗中的行动灵活无比",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 700L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 100L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.MAGICIAN.getJobCode()
    ),
    EQUIPMENT_314(314L, "沧海秘典",
            "圣城普通星术典籍之一，沧海浩瀚，能够显著增强使用者的星术感应能力，从而增加其星术的威力",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 1000L)),
            EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.MAGICIAN.getJobCode()
    ),
    EQUIPMENT_315(315L, "星轨秘典",
            "圣城普通星术典籍之一，战斗时会诞生星轨辅助使用者，使其星术命中率显著提升",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 700L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 100L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.MAGICIAN.getJobCode()
    ),
    EQUIPMENT_316(316L, "窃魂秘典",
            "一位星术天才堕入邪途后创造的星术典籍，融合了邪修的噬魂之力后，使使用者能够通过吞噬灵魂来强化自身",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 800L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.LIFE_STEALING, 0.1F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.MAGICIAN.getJobCode()
    ),
    EQUIPMENT_317(317L, "噬星之龙（残）",
            "传说为灭世的十六人中的副手窃星神君所用的星术神典残卷，曾经一度遮蔽了星空，即使只剩一页纸也能散发无与伦比的威力",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 2000L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 300L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.1F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_PENETRATE, 0.1F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.LIFE_STEALING, 0.1F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.MAGICIAN.getJobCode()
    ),
    EQUIPMENT_318(318L, "打神棍",
            "老神棍自己制造的打神棍，号称与神器打神鞭同源，为戒神之器。其实只不过是一个用灵材削成的棍子罢了",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 500L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, 150L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.CHEATER.getJobCode()
    ),
    EQUIPMENT_319(319L, "行者武棍",
            "模仿传说中的神器定海神针所做的量产武棍，除了会伸缩之外和其它灵棍没什么区别",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 800L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 80L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.CHEATER.getJobCode()
    ),
    EQUIPMENT_320(320L, "爆破棍",
            "未元之都开发的量产机械棍，其内置爆破装置，在攻击时可以引爆，造成爆破伤害",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.2F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.CHEATER.getJobCode()
    ),
    EQUIPMENT_321(321L, "松竹棍",
            "老神棍用其蕴养的灵植松竹所制作的灵棍，松竹灵威能天然形成保护层，削弱即将到来的伤害。而同样因为灵威的隔绝，" +
                    "松竹棍在攻击性能上大打折扣，成为了一根偏防御性质的灵棍",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 200L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 300L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 100L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, 50L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.CHEATER.getJobCode()
    ),
    EQUIPMENT_322(322L, "举案棍",
            "江湖雌雄双盗之一“鸿案大盗”所持灵棍，相传该棍与“齐眉棍”为一对雌雄棍，是传奇匠人“千秋和”的手笔，雌雄合璧可爆发惊人威能，合璧方法现已失传",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 700L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 120L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.CHEATER.getJobCode()
    ),
    EQUIPMENT_323(323L, "齐眉棍",
            "江湖雌雄双盗之一“相庄大盗”所持灵棍，相传该棍与“举案棍”为一对雌雄棍，是传奇匠人“千秋和”的手笔，雌雄合璧可爆发惊人威能，合璧方法现已失传",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 500L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, 100L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 50L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.CHEATER.getJobCode()
    ),
    EQUIPMENT_324(324L, "深渊机械弓A型",
            "以天才机械师Abyss八岁时制作的Ab机械弓为原型，加以改良投入实战的量产型战斗机械弓，是一把经典的速射弓",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 400L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 150L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.PENETRATE, 0.1F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.GUN.getJobCode()
    ),
    EQUIPMENT_325(325L, "深渊机械弓B型",
            "以天才机械师Abyss八岁时制作的Ab机械弓为原型，加以改良投入实战的量产型战斗机械弓，相较于A型增加了威力和稳定性，" +
                    "但相应的增加了拉弓的难度，使其不再适合普通人进行速射",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 100L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.PENETRATE, 0.1F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.GUN.getJobCode()
    ),
    EQUIPMENT_326(326L, "高能光枪α型",
            "以激发高能激光作为主要攻击手段的量产式机械步枪。初版的高能光枪较为笨重，携带不便，但是由于高能激光的特性，使其一旦瞄准便可轻松命中敌人造成伤害",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 150L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.15F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.GUN.getJobCode()
    ),
    EQUIPMENT_327(327L, "光束步枪α型",
            "以连续激发小型激光束作为主要攻击手段的量产式机械步枪。较为轻便易于携带，射速较快但稳定性不足",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 400L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 120L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.1F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.GUN.getJobCode()
    ),
    EQUIPMENT_328(328L, "炮狙一式",
            "护甲铁胄的天敌，炮狙一式的性能相较于其他灵枪最主要的优势就是它绝对的穿透属性，重甲在它面前可能只是穿了一层薄薄的纸片",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.PENETRATE, 0.25F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.GUN.getJobCode()
    ),
    EQUIPMENT_329(329L, "星滞",
            "世界上第一把可控星能机械枪，但似乎并没有能够发挥出星术的威力，仅仅是把星术当成了一种能量击发出去而已",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 100L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.PENETRATE, 0.1F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.GUN.getJobCode()
    ),
    EQUIPMENT_330(330L, "魔粹地炎剑",
            "高品质的地炎剑经过邪修功法淬炼而成的魔剑，相比于普通地炎剑要更加暴力和嗜血",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 800L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.25F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.EVIL.getJobCode()
    ),
    EQUIPMENT_331(331L, "天无涯",
            "陪伴邪修长大的魔剑，无人知晓其来历。尽管只是一把灵级武器，却能让人对其生起来自本源的恐惧",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 1000L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 120L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.LIFE_STEALING, 0.1F)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.EVIL.getJobCode()
    ),
    EQUIPMENT_332(332L, "量产型三阶剑符",
            "因为需要注入灵魂之力，无法工业化生产，一位制符大师一天可以制作5-10张",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 600L)),
            EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode()
    ),
    EQUIPMENT_333(333L, "岁子银甲",
            "修真王朝王室邀请传奇匠人千秋和为其军队打造的量产型盔甲，具有一定的防御力，用材比较轻便，使军队不至于过多损失其机动性和灵活性",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 1250L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 850L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE_REDUCTION, 0.3F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HP, 30000L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.ARMOR
    ),
    EQUIPMENT_334(334L, "皓月甲",
            "因其胸前的一轮皓月标记而得名，修真王朝重甲兵所装备的盔甲，十分厚重，有很强的防御能力，" +
                    "缺点是过于沉重，对于士兵的体力有很大的消耗，也会使他们行动有所不便",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 1500L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 1500L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HP, 50000L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, -100L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.ARMOR
    ),
    EQUIPMENT_335(335L, "真武甲",
            "受到真武之石照临的铠甲，蕴含真武的生机之力，能够显著提升使用者的受治疗效果。",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 850L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 850L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HP, 80000L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.ARMOR
    ),
    EQUIPMENT_336(336L, "芳隐绫",
            "由天丛蚕丝纺织成的长绫，能够隐去身形，迷乱敌方的行动，对星术有着一定的抵抗能力，但很容易被刀剑斩碎，十分柔弱，不适合近身作战",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, 800L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 900L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HP, 30000L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.ARMOR
    ),
    EQUIPMENT_337(337L, "弥身隍行屐",
            "首件量产化的飞行鞋子，即使是普通人也能轻易催动，腾云驾雾不再是梦",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 350L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 700L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 700L)
            ), EquipmentRankEnum.DYNAMIC, EquipmentTypeEnum.ARMOR
    ),

    EQUIPMENT_400(400L, "对玄双子",
            "传奇匠人千秋和制作的一对阴阳双持剑，两柄剑分别以玄阴珠和明阳珠为引，以玄清双生石的子石为身。战斗中相辅相成，甚至能够催动一小部分阴阳法则的力量为己用。是千秋和的成名作之一" +
                    "\n隐藏技能\n阴阳轮转：战斗开始时提高自身速度值10%，持续三回合",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 1500L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 300L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.15F)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeBattle(List<String> battleMsg) {
                    StringBuilder builder = new StringBuilder("※")
                            .append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的武器技能【阴阳轮转】被触发");
                    BuffUtil.addBuff(this.getFrom(),
                            new BuffDTO(BuffTypeEnum.SPEED, "", 0.1F), 3, builder);
                    battleMsg.add(builder.toString());
                }
            }
    ),
    EQUIPMENT_401(401L, "魔蛇之首",
            "用天级魔物灾厄魔蛇被讨伐后遗留的头颅制作的魔剑，散发着不详和灾厄的气息。据传，这把剑在制作时因魔蛇的神识无法完全剔除，而使其从一把灵剑堕为魔剑。" +
                    "但同样因为如此，这把剑一直有着可以催动魔蛇力量的传闻，但因其历代使用者均因不明原因惨死，此传闻一直也没有得到证实" +
                    "\n隐藏技能\n毒牙：攻击有5%概率造成中毒效果持续两回合，使敌方降低10%攻击同时每回合流失生命上限5%的生命值",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 1800L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.LIFE_STEALING, 0.1F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.25F)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage, boolean critical, StringBuilder builder) {
                    if (RandomUtil.isHit(0.05F)) {
                        builder.append("，").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                                .append("的武器技能【毒牙】被触发");
                        BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.ATK, "中毒", -0.1F, true), 2, builder);
                        BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.BLEEDING, "中毒", -0.05F, true), 2, builder);
                    }
                }
            }
    ),
    EQUIPMENT_402(402L, "五色斑霞",
            "修仙王朝明霞道人的佩剑，因其战斗中能散发五色神光蔽人耳目扰人心神而得名。相传，还是一介散修明霞道人在返虚期偶遇一太古秘境并夺此至宝，" +
                    "从此修为突飞猛进，后为修仙王朝发掘，成为修仙王朝的顶尖战力之一" +
                    "\n隐藏技能\n眩灵光子：战斗开始时提高自身10%闪避，持续三回合",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 1500L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, 400L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 100L)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeBattle(List<String> battleMsg) {
                    StringBuilder builder = new StringBuilder("※")
                            .append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的武器【五色斑霞】被触发");
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.DODGE, "", 0.1F), 3, builder);
                    battleMsg.add(builder.toString());
                }
            }
    ),
    EQUIPMENT_403(403L, "太衍镇山石",
            "本不该出现于太衍圣山灵阵上的一块极为突兀的石碑，极具镇压之力。其上镌刻着一段文字“莫道灵山圣人迹，却话宝幼鬼山逢。”" +
                    "修真者将其从灵阵上拔出后镇压之力逐渐收敛，化作石剑相。彼时，太衍圣山也逐渐现出了它本来的面貌" +
                    "\n隐藏技能\n镇山之力：战斗开始时，对方全体速度-800，持续三回合",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 1500L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, -300L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, -300L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, -500L)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeBattle(List<String> battleMsg) {
                    StringBuilder builder = new StringBuilder("※")
                            .append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的武器技能【镇山之力】被触发");
                    this.getEnemy().forEach(enemy ->
                            BuffUtil.addBuff(enemy, new BuffDTO(BuffTypeEnum.SPEED, "", -800L), 3, builder));
                    battleMsg.add(builder.toString());
                }
            }
    ),
    EQUIPMENT_404(404L, "紫云天",
            "修仙王朝下属天奔雷教至宝，天奔雷法与紫云金母石的绝妙结合，紫玉金母石可以转化普通灵气为封存的天奔雷法充能，" +
                    "而以天奔雷法催生的紫云也将蕴含雷法威能，使紫云在战斗中出其不意地对敌方造成伤害" +
                    "\n隐藏技能\n蔽天云紫：攻击造成暴击伤害时对敌方全体造成120%物理伤害（不可暴击），若该伤害命中，则额外降低命中单体10%命中，持续一回合",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 1600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.35F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE, 0.5F)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON, JobEnum.XIU_ZHEN.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage, boolean critical, StringBuilder builder) {
                    if (critical) {
                        boolean isHit = BattleUtil.isHit(this.getFrom(), tar, this.getOur(), this.getEnemy(),
                                new ArrayList<>(), null, builder);
                        if (isHit) {
                            builder.append("，").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                                    .append("的武器技能【蔽天云紫】被触发");
                            long extraDamage = BattleUtil.getDamage(this.getFrom(), tar, this.getOur(), this.getEnemy(),
                                    DamageTypeEnum.ATK, 0.8F, null, new ArrayList<>(), builder);
                            builder.append("，追加造成").append(extraDamage).append("点伤害");
                            BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.HIT, "", -0.1F), 1, builder);
                        }
                    }
                }
            }
    ),
    EQUIPMENT_405(405L, "狼牙",
            "沃尔夫雇佣兵团长德隆的佩刀，由噬魂狼王的牙打造而成，极为厚重和锋利。据本人所言，狼牙为其祖辈代代相传之宝，" +
                    "是其祖先沃尔夫族长抵御兽潮获得的战利品，是荣耀的象征。但其祖先后来不顾族人劝阻追随“灭世的十六人”，在“灭世的十六人”遭遇围剿后，" +
                    "其祖先亦被行刑，从此沃尔夫家族一落千丈" +
                    "\n隐藏技能\n牙狼噬：自身攻击命中敌方时对敌方造成10%防御降低效果，持续两回合",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 2200L)),
            EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON, JobEnum.SI_DI_WU_SHI.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage, boolean critical, StringBuilder builder) {
                    builder.append("，").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的武器技能【牙狼噬】被触发");
                    BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.DEF, "", -0.1F), 2, builder);
                }
            }
    ),
    EQUIPMENT_406(406L, "凌锋·细雪",
            "在死烬之渊.终焉战场上屹立不倒的两把名刀，曾经冠绝一个世代，蔽日岛大将垚千寂的佩刀。据传，垚千寂也曾追随“灭世的十六人”，" +
                    "并按照他们的指示前往死烬之渊，并最终导致了蔽日岛的覆灭。还有种说法称，垚千寂意图颠覆“灭世的十六人”所统领的组织，" +
                    "因而前往死烬之渊欲寻找神器“冰海沉星”，但其错估了死烬之渊的危险性，导致蔽日岛核心力量尽数殒命，最终导致了蔽日岛的覆灭" +
                    "\n隐藏技能\n凌锋：对敌方造成伤害时提高自身攻击力10%，持续2回合\n细雪：闪避敌方攻击后回复自身体力5%",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 1200L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 350L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, 350L)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON, JobEnum.SI_DI_WU_SHI.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void ifNotHit(BattleDTO from, BattleDTO tar, SkillEnum skillEnum, StringBuilder builder) {
                    super.ifNotHit(from, tar, skillEnum, builder);
                    if (this.getFrom().equals(tar)) {
                        OrganismDTO fromOrganism = from.getOrganismInfoDTO().getOrganismDTO();
                        BattleUtil.doHealing(from, Math.round(fromOrganism.getHpWithAddition() * 0.05), builder);
                    }
                }

                @Override
                public void processIfHit(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage, boolean critical, StringBuilder builder) {
                    if (damage.get() > 0) {
                        builder.append("，").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                                .append("的武器技能【凌锋】被触发");
                        BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "", 0.1F), 2, builder);
                    }
                }
            }
    ),
    EQUIPMENT_407(407L, "真武之极",
            "传奇匠人千秋和的成名作之一，最初命名其为“极”意味着这把刀是千秋和所作刀具之极，但尽管如此，千秋和对其仍不满意，" +
                    "他认为这把刀具过于轻浮而缺乏凌厉，因此他找到修仙王朝希望借助其至宝真武之石的淬炼，将其真正打造成万古无一的刀具之“极”。" +
                    "经历过真武之石淬炼的“极”脱胎换骨，更加轻盈但锋华不减，更重要的是，它散发出了一丝法则的波纹，能够造成无视甲胄直击实体的伤害。" +
                    "为感谢修仙王朝的倾囊相助，千秋和也是将这把名刀更名为“真武之极”" +
                    "\n隐藏技能\n极道：攻击命中时额外附加敌方当前最大生命值3%的真实伤害",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 1600L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 250L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 200L)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON, JobEnum.SI_DI_WU_SHI.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage, boolean critical, StringBuilder builder) {
                    long extraDamage = Math.round(tar.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition() * 0.03);
                    damage.addAndGet(extraDamage);
                    builder.append("，").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的武器技能【极道】被触发，额外造成").append(extraDamage).append("点伤害");
                }
            }
    ),
    EQUIPMENT_408(408L, "星辉礼赞",
            "圣城一级星祭的标配典籍，虽是量产化典籍，但其制作成本极其高昂，且均经过圣座缘星圣主的亲自赋能，使其直接一跃成为通玄级法器。" +
                    "再者，星辉礼赞的强大之处在于其群体作战的强大实力，通过法器之间的互相加持，能使使用者群体获得无与伦比的强大攻击力，" +
                    "即使在越阶挑战的情况下，敌方也很难在星辉礼赞群的攻击下全身而退" +
                    "\n隐藏技能\n星庭献礼：对敌方造成伤害时提升己方全体成员10%法伤和10%攻击持续一回合",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 2000L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 300L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_PENETRATE, 0.1F)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON, JobEnum.MAGICIAN.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage, boolean critical, StringBuilder builder) {
                    if (damage.get() > 0) {
                        builder.append("，").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                                .append("的武器技能【星庭献礼】被触发");
                        this.getOur().forEach(o -> {
                            BuffUtil.addBuff(o, new BuffDTO(BuffTypeEnum.ATK, "", 0.1F), 1, builder);
                            BuffUtil.addBuff(o, new BuffDTO(BuffTypeEnum.MAGIC_ATK, "", 0.1F), 1, builder);
                        });
                    }
                }
            }
    ),
    EQUIPMENT_409(409L, "净天无涯",
            "陪伴邪修长大的魔剑——天无涯，吸收了四大魔器之一的摩诃无量的残片之后，突破枷锁成长为通玄级。不仅解放了自身本源古老魔器的部分力量，" +
                    "而且继承了摩诃无量的部分法相之力，其恐怖程度可见一斑" +
                    "\n隐藏技能\n摩诃无量：战斗开始时，攻击力提升80% ，穿甲提升50%持续六回合",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 2200L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 240L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.LIFE_STEALING, 0.2F)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON, JobEnum.EVIL.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeBattle(List<String> battleMsg) {
                    StringBuilder builder = new StringBuilder("※")
                            .append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的武器技能【摩诃无量】被触发");
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "摩诃无量", 0.8F), 6, builder);
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.PENETRATE, "摩诃无量", 0.5F), 6, builder);
                    battleMsg.add(builder.toString());
                }
            }
    ),
    EQUIPMENT_410(410L, "开山刀-真",
            "真正的开山刀，著名刀侠“白龙刀客”的武器。" +
                    "白龙刀客曾持此刀于死烬之渊西侧的天火山脉与道枯真人的一战，白龙刀客用此刀劈开了一座山，引发天火降世，道枯真人重伤逃跑，白龙刀客就此下落不明。" +
                    "后来，此刀出现在了死烬之渊东侧的火树林中，由此可见，白龙刀客凶多吉少" +
                    "\n隐藏技能：每次攻击，都有5%概率附加一次10%物攻的真实伤害",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 2200L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE, 0.4F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.1F)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON, JobEnum.SI_DI_WU_SHI.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage, boolean critical, StringBuilder builder) {
                    long extraDamage = Math.round(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getAtk() * 0.1);
                    if ((new Random().nextInt(100) < 5)) {
                        damage.addAndGet(extraDamage);
                    }
                    builder.append("，").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的武器技能被触发，额外造成").append(extraDamage).append("点伤害");
                }
            }
    ),
    EQUIPMENT_411(411L, "白龙刀",
            "真正的开山刀，著名刀侠“白龙刀客”的武器。" +
                    "白龙刀客成名之战中使用的武器" +
                    "白龙刀客是双持刀客，通常左手使用此刀，右手使用开山刀" +
                    "白龙刀客最终一战后，此刀下落不明" +
                    "\n隐藏技能：每次攻击，都有20%的概率附加一次20%法攻的普通伤害",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 3000L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 0.1F)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON, JobEnum.SI_DI_WU_SHI.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage, boolean critical, StringBuilder builder) {
                    long extraDamage = Math.round(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getMagicAtk() * 0.2);
                    if ((new Random().nextInt(100) < 20)) {
                        damage.addAndGet(extraDamage);
                    }
                    builder.append("，").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的武器技能被触发，额外造成").append(extraDamage).append("点伤害");
                }
            }
    ),
    EQUIPMENT_412(412L, "浣溪沙",
                    "修仙王朝眉山仙子的轻纱，在仙子常年保养与锤炼下已近神迹，" +
                    "生有灵智，可自动护主。材质坚韧，" +
                    "由千年晶水蓝矿打磨成的丝织而成，是兼顾颜值与实力的强力防具" +
                    "\n隐藏技能\n兰芽短浸：受到伤害后，闪避提升35%持续两回合",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 5200L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 2300L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HP, 200000L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 500L)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.ARMOR,
            new ExtraBattleProcessTemplate() {
                @Override
                public void afterDamage(BattleDTO tar, AtomicLong atoDamage, StringBuilder builder) {
                    if (atoDamage.get() > 0) {
                        builder.append("，").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                                .append("的武器技能【兰芽短浸】被触发");
                        BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.DODGE, "", 0.35F), 2, builder);
                    }
                }
            }
    ),
    EQUIPMENT_413(413L, "雨倾纸伞",
            "江上流传着一则伞和剑的传说，两位素未谋面的痴人，于九日凌空之日，共赴黄泉。" +
                    "伞啊，你在吗，你看这九日凌空的异象，是我为你庆生的贺礼。\n" +
                    "剑啊，我在，唯此一生，我愿为你，与世界为敌。\n" +
                    "有道是：天临渊火，流影灭之焚形；\n" +
                    "        伞倾亲意，霜雪骇人离醉。\n" +
                    "        盈雷齐鼓，剑彻独锋。\n" +
                    "        霜侵血路，寒林遍地。\n" +
                    "        所亲之所扰，所妄之所达。\n" +
                    "        所倾之所绕，所败之所消。\n" +
                    "        冥河故里饮归处，饶是陌路，对影成三人。" +
                    "\n套装效果 九临清河晏：\n" +
                    "与旭日神剑同时装备时，二者提供属性翻倍，旭日神剑技能效果持续整场战斗。",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 2200L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 500L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE, 0.8F)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.JEWELRY,
            new ExtraBattleProcessTemplate() {
            }
    ),
    EQUIPMENT_414(414L, "旭日神剑",
            "江上流传着一则伞和剑的传说，两位素未谋面的痴人，于九日凌空之日，共赴黄泉。" +
                    "伞啊，你在吗，你看这九日凌空的异象，是我为你庆生的贺礼。\n" +
                    "剑啊，我在，唯此一生，我愿为你，与世界为敌。\n" +
                    "有道是：天临渊火，流影灭之焚形；\n" +
                    "        伞倾亲意，霜雪骇人离醉。\n" +
                    "        盈雷齐鼓，剑彻独锋。\n" +
                    "        霜侵血路，寒林遍地。\n" +
                    "        所亲之所扰，所妄之所达。\n" +
                    "        所倾之所绕，所败之所消。\n" +
                    "        冥河故里饮归处，饶是陌路，对影成三人。" +
                    "\n隐藏技能 九日临空：\n" +
                    "战斗开始时，提升自身15%攻击力持续九回合，九回合内，每次攻击命中后有15%概率使敌方陷入焚毁异常持续一回合。",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 2800L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.25F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 250L)
            ), EquipmentRankEnum.MYSTERY, EquipmentTypeEnum.WEAPON,
            new ExtraBattleProcessTemplate() {
            }
    ),

    EQUIPMENT_500(500L, "彼岸·净天无涯",
            "陪伴邪修长大的魔剑——天无涯的完全形态，四大魔器之初，零号魔器——天喑无道上掉落的碎屑打造的仿制品，拥有同源于天喑无道的力量，除开本源之外，" +
                    "其本体由无垠之精——一种域外神铁打造而成\n隐藏技能\n神·摩诃无量：战斗开始时提升自身攻击力150% 防御力20% 穿甲50%持续12回合",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 5500L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 2100L, 0.2F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 2100L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.LIFE_STEALING, 0.3F)
            ), EquipmentRankEnum.WONDER, EquipmentTypeEnum.WEAPON, JobEnum.EVIL.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeBattle(List<String> battleMsg) {
                    StringBuilder builder = new StringBuilder("※")
                            .append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的武器技能【神·摩诃无量】被触发");
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "摩诃无量", 1.5F), 12, builder);
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.DEF, "摩诃无量", 0.2F), 12, builder);
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.PENETRATE, "摩诃无量", 0.5F), 12, builder);
                    battleMsg.add(builder.toString());
                }
            }
    ),
    EQUIPMENT_501(501L, "星神·沧白之祈",
            "审判双神之一——星神.沧白之祈的分身， 在获得了其认可之后获得的星神权能\n" +
                    "隐藏技能\n明神：沧白之祈每三回合降下神谕，我的行动前解除自身所处的异常状态和弱化状态，并提升自身40%闪避持续2回合",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 7000L, 0.5F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 7000L, 0.5F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.BEHAVIOR_SPEED, 1850L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.65F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE, 1F)
            ), EquipmentRankEnum.WONDER, EquipmentTypeEnum.WEAPON,
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeMyBehavior(BattleDTO tar, StringBuilder builder) {
                    Map<String, Integer> markMap = this.getFrom().getMarkMap();
                    if (!markMap.containsKey("沧白之祈") || markMap.get("沧白之祈") == 0) {
                        markMap.put("沧白之祈", 3);
                        BuffUtil.clearAbnormalBuff(this.getFrom(), builder);
                        BuffUtil.clearInactiveBuff(this.getFrom(), builder);
                        BuffUtil.addBuff(this.getFrom(),
                                new BuffDTO(BuffTypeEnum.DODGE, "沧白之祈", 0.4F), 2, builder);
                    } else {
                        markMap.put("沧白之祈", markMap.get("沧白之祈") - 1);
                    }
                }
            }
    ),
    EQUIPMENT_502(502L, "寒星.圣者之冠",
            "以冰海沉星为原型，并针对其特性打造出来的神煅之器，为古圣城圣者权位与力量的标志。\n" +
                 "传闻圣者之冠具有锁定冰海沉星所在位置的能力，古圣城历代圣者都有着寻找冰海沉星的记录，但他们最终都无功而返或身死道消。\n" +
                 "隐藏技能\n圣者尊崇.零度法则:装备者受到的伤害减少20%，攻击命中后使敌方速度降低15%持续一回合。"+
                 "神煅加护\n寒星之赐\n法抗+20%，法强+15%",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 10000L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 0.2F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 15000L, 0.15F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_REDUCTION_RATE, 0.35F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE_REDUCTION, 0.80F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HP, 400000F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MP, 250F)
            ), EquipmentRankEnum.WONDER, EquipmentTypeEnum.ARMOR,
            new ExtraBattleProcessTemplate() {
            }
    ),

    EQUIPMENT_600(600L, "冰海沉星",
            "传闻，死烬之渊的深处是一片蔚蓝的海洋，而在这片海域的中央，沉没着一颗璀璨的明星。" +
                    "无人知道它真正的样貌为何，因为那些不顾死活而觊觎这颗明珠的人们，已经全部死在了它可怕的凛寒之下" +
                    "\n隐藏技能\n永冻世界：攻击命中时提升自身30%法伤持续一回合，若该次攻击未命中，则提升自身30%命中（持续一回合）后对敌方全体造成冻结持续一回合" +
                    "\n法则之力\n零度法则：进入战斗后我方全体成员提升20%速度，敌方全体成员降低20%速度，且我方成员对冻结状态敌人造成的伤害额外提升50%",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 15000L, 0.3F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 1.0F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE, 2.0F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_PENETRATE, 0.3F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 2100L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, 0.3F)
            ), EquipmentRankEnum.RULE, EquipmentTypeEnum.JEWELRY,
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeBattle(List<String> battleMsg) {
                    String name = this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName();
                    StringBuilder builder = new StringBuilder("※" + name + "的饰品法则【零度法则】被触发");
                    this.getOur().forEach(o -> RuleUtil.addRule(o, OrganismPropertiesEnum.BEHAVIOR_SPEED, "零度法则", 0.2F, builder));
                    this.getEnemy().forEach(o -> RuleUtil.addRule(o, OrganismPropertiesEnum.BEHAVIOR_SPEED, "零度法则", -0.2F, builder));
                    battleMsg.add(builder.toString());
                }

                @Override
                public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage, boolean critical, StringBuilder builder) {
                    builder.append("，").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的饰品技能【永冻世界】被触发");
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.MAGIC_ATK, "", 0.3F), 1, builder);
                }

                @Override
                public void processIfOurHit(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect,
                                            AtomicLong damage, boolean critical, StringBuilder builder) {
                    List<BattleBuffDTO> buffList = tar.getBuffMap().get(BuffTypeEnum.PAUSE);
                    if (CollectionUtils.isNotEmpty(buffList) &&
                            buffList.stream().anyMatch(buff -> "冻结".equals(buff.getBuffDTO().getBuffName()))) {
                        builder.append("，饰品法则【零度法则】被触发，伤害提升至1.5倍");
                        damage.set(Math.round(damage.get() * 1.5));
                    }
                }

                @Override
                public void processIfNotHit(BattleDTO tar, SkillEnum skillEnum, StringBuilder builder) {
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.HIT, "永冻世界", 0.3F), 1, builder);
                    this.getEnemy().forEach(enemy -> {
                        BuffUtil.addBuff(enemy, new BuffDTO(BuffTypeEnum.PAUSE, "冻结", 0.3F, true), 1, builder);
                        BuffUtil.addBuff(enemy, new BuffDTO(BuffTypeEnum.SPEED, "永冻世界", -0.1F), 2, builder);
                    });
                }
            }
    ),
    EQUIPMENT_601(601L, "反物质弹",
            "未元之都利用湮灭法则开发出的产物。"+
            "作为世间最具毁灭力量的法则集合体，又融合了反物质的科技手段，反物质弹尚处于雏形之时就已经达到了惊人的法则级，其毁灭之能可以无视一切防御直达命脉。"+
            "标志着普通人类的脚步已经踏足了世界的根基。"+
            "\n法则之力\n湮灭法则：所有目标为敌方，且有伤害倍率的单次攻击，附加200%物攻的真实伤害",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 20000L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 1F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE, 4F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.PENETRATE, 0.7F)
            ), EquipmentRankEnum.RULE, EquipmentTypeEnum.WEAPON, JobEnum.GUN.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect,
                                              AtomicLong damage, boolean critical, StringBuilder builder) {
                    OrganismDTO organism = this.getFrom().getOrganismInfoDTO().getOrganismDTO();
                    builder.append("，").append(organism.getName()).append("的武器法则【湮灭法则】被触发");
                    BattleUtil.doRealDamage(tar, 2 * organism.getAtk(), builder);
                }
            }
    ),
    EQUIPMENT_602(602L, "不灭金身",
            "法则之力\n不灭法则：每次战斗限一次，受到致命伤害后，免疫此次伤害"+
                    "神煅加护\n固若金汤：物防+15% 法抗+15%"+
                    "隐藏技能\n" +
                    "金身虚影：战斗开始时，除真实伤害外的所有伤害，按伤害类型，减少对应自身（防御或法抗）200%的数值，持续8回合。金身法则触发后，重新激活此技能，持续3回合。",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HP, 800000L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DEF, 32000L, 0.15F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_DEF, 32000L, 0.15F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_REDUCTION_RATE, 0.5F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE_REDUCTION, 1.5F)
            ), EquipmentRankEnum.RULE, EquipmentTypeEnum.ARMOR,
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeMeDeath(BattleDTO from, BattleDTO tar,
                                          AtomicLong atoDamage, StringBuilder builder) {

                    OrganismDTO organism = tar.getOrganismInfoDTO().getOrganismDTO();
                    builder.append("，").append(organism.getName()).append("的武器法则【不灭法则】被触发");
                }
            }
    ),
    EQUIPMENT_603(603L, "天喑无道",
            "原始魔器，四大魔器的母本，此世全部负面力量的集合体."+
            "其历史无从考证，只知其十分古老，"+
            "最早记录于星历前355年，圣星府灭门案现场被人目击，由一名十岁左右的孩童携带逃离，随后不知踪迹。"+
            "随后几十年间，广有天喑无道之名的传闻，且传闻所在之处同时有数起凶案发生。"+
            "第一次被证实存在于星历前263年，一名神秘人士将其置于圣城拍卖会拍卖，后被圣城高层拍下。星历前3年，天喑无道失窃，随后不知所踪。"+
            "法则之力\n泯灭法则：敌方全体血量回复效果降低90%持续整场战斗无法消除"+
            "隐藏技能\n" +
            "觉.摩诃无量\n" +
            "战斗开始时提升自身100%攻击力 50%命中 50%穿甲 50%防御 50%法抗 持续整场战斗，若该buff效果被清除，则五回合后重新激活。所有摩诃无量同名效果只触发一条，且（觉>神>普>残）\n" +
            "魔慑诡计\n" +
            "谋天化道：装备者攻击命中后使敌方受到的伤害提升15%。",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HP, 200000L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.ATK, 12000L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.LIFE_STEALING, 0.8F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 0.8F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE, 1.5F)
            ), EquipmentRankEnum.RULE, EquipmentTypeEnum.JEWELRY,
            new ExtraBattleProcessTemplate() {
                @Override
                public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect,
                                              AtomicLong damage, boolean critical, StringBuilder builder) {
                    OrganismDTO organism = this.getFrom().getOrganismInfoDTO().getOrganismDTO();
                    builder.append("，").append(organism.getName()).append("的武器法则【泯灭法则】被触发");
                }
            }
    ),
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
        this.limitLevel = equipmentRankEnum.getDefaultPlayerLimitLevel();
        this.desc = desc;
        this.prop = prop;
        this.equipmentRankEnum = equipmentRankEnum;
        this.equipmentTypeEnum = equipmentTypeEnum;
        this.limitJob = Collections.singletonList(Constants.ALL);
        this.extraBattleProcessTemplate = new ExtraBattleProcessTemplate() {
        };
    }

    EquipmentEnum(Long id, String name, String desc, List<EquipmentPropertiesDTO> prop,
                  EquipmentRankEnum equipmentRankEnum, EquipmentTypeEnum equipmentTypeEnum, String jobCode) {
        this.id = id;
        this.name = name;
        this.limitLevel = equipmentRankEnum.getDefaultPlayerLimitLevel();
        this.desc = desc;
        this.prop = prop;
        this.equipmentRankEnum = equipmentRankEnum;
        this.equipmentTypeEnum = equipmentTypeEnum;
        this.limitJob = Collections.singletonList(jobCode);
        this.extraBattleProcessTemplate = new ExtraBattleProcessTemplate() {
        };
    }

    EquipmentEnum(Long id, String name, String desc, List<EquipmentPropertiesDTO> prop,
                  EquipmentRankEnum equipmentRankEnum, EquipmentTypeEnum equipmentTypeEnum, String jobCode,
                  ExtraBattleProcessTemplate template) {
        this.id = id;
        this.name = name;
        this.limitLevel = equipmentRankEnum.getDefaultPlayerLimitLevel();
        this.desc = desc;
        this.prop = prop;
        this.equipmentRankEnum = equipmentRankEnum;
        this.equipmentTypeEnum = equipmentTypeEnum;
        this.limitJob = Collections.singletonList(jobCode);
        this.extraBattleProcessTemplate = template;
    }

    EquipmentEnum(Long id, String name, String desc, List<EquipmentPropertiesDTO> prop,
                  EquipmentRankEnum equipmentRankEnum, EquipmentTypeEnum equipmentTypeEnum, List<String> jobCode) {
        this.id = id;
        this.name = name;
        this.limitLevel = equipmentRankEnum.getDefaultPlayerLimitLevel();
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
        this.limitLevel = equipmentRankEnum.getDefaultPlayerLimitLevel();
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
        this.limitLevel = equipmentRankEnum.getDefaultPlayerLimitLevel();
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
        EquipmentEnum equipmentEnum = EQUIPMENT_ENUM_MAP.get(id);
        if (Objects.isNull(equipmentEnum)) {
            throw EquipmentExceptionEnum.EQUIPMENT_NOT_EXIST.getException();
        }
        return equipmentEnum;
    }

    public static EquipmentEnum getByName(String name) {
        EquipmentEnum equipmentEnum = EQUIPMENT_ENUM_NAME_MAP.get(name);
        if (Objects.isNull(equipmentEnum)) {
            throw EquipmentExceptionEnum.EQUIPMENT_NOT_EXIST.getException();
        }
        return equipmentEnum;
    }

    static {
        EQUIPMENT_ENUM_MAP = Arrays.stream(EquipmentEnum.values()).collect(Collectors.toMap(
                EquipmentEnum::getId, Function.identity()));
        EQUIPMENT_ENUM_NAME_MAP = Arrays.stream(EquipmentEnum.values()).collect(Collectors.toMap(
                EquipmentEnum::getName, Function.identity()));
    }
}
