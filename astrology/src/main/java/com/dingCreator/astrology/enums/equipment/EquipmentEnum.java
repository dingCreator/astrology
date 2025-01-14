package com.dingCreator.astrology.enums.equipment;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.BattleBuffDTO;
import com.dingCreator.astrology.dto.BattleDTO;
import com.dingCreator.astrology.dto.BuffDTO;
import com.dingCreator.astrology.dto.equipment.EquipmentPropertiesDTO;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import com.dingCreator.astrology.enums.OrganismPropertiesEnum;
import com.dingCreator.astrology.enums.exception.EquipmentExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.template.ExtraBattleProcessTemplate;
import com.dingCreator.astrology.util.BuffUtil;
import com.dingCreator.astrology.util.RankUtil;
import com.dingCreator.astrology.util.RuleUtil;
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
    EQUIPMENT_208(208L, "量产型8086芯片",
            "某处遗迹中找到的唯一还能用的一块芯片，可以用于计算对方的移动轨迹，增加一定的命中率，一起找到的还有某块储存设备，读取到了以下内容：" +
                    "“（此处请根据故事背景，插入一些信息）”",
            Collections.singletonList(new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 100L)),
            EquipmentRankEnum.NORMAL, EquipmentTypeEnum.WEAPON, JobEnum.GUN.getJobCode()
    ),
    EQUIPMENT_209(209L, "量产型阿卡",
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
                            .append("的武器【对玄双子】被触发");
                    BuffUtil.addBuff(this.getFrom(),
                            new BuffDTO(BuffTypeEnum.SPEED, "", 0.1F, false), 3, builder);
                    battleMsg.add(builder.toString());
                }
            }
    ),
    EQUIPMENT_41(41L, "魔蛇之首 ：攻击力+1800 吸血+10% 暴击率+25%\n",
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
                public void processIfHit(BattleDTO tar, SkillEnum skillEnum, AtomicLong damage, StringBuilder builder) {

                }
            }
    ),
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
    EQUIPMENT_600(600L, "冰海沉星",
            "隐藏技能\n" +
                    "永冻世界：攻击命中时提升自身30%法伤持续一回合，若该次攻击未命中，则提升自身30%命中（持续一回合）后对敌方全体造成冻结持续一回合\n" +
                    "法则之力\n" +
                    "零度法则：进入战斗后我方全体成员提升20%速度，敌方全体成员降低20%速度，且我方成员对冻结状态敌人造成的伤害额外提升50%",
            Arrays.asList(
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_ATK, 15000L, 0.3F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_RATE, 1.0F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.CRITICAL_DAMAGE, 2.0F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.MAGIC_PENETRATE, 0.3F),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.HIT, 2100L),
                    new EquipmentPropertiesDTO(EquipmentPropertiesTypeEnum.DODGE, 0.3F)
            ), EquipmentRankEnum.RULE, EquipmentTypeEnum.JEWELRY, JobEnum.MAGICIAN.getJobCode(),
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeBattle(List<String> battleMsg) {
                    String name = this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName();
                    StringBuilder builder = new StringBuilder("※" + name + "的饰品【冰海沉星】效果触发");
                    this.getOur().forEach(o -> RuleUtil.addRule(o, OrganismPropertiesEnum.BEHAVIOR_SPEED, "零度法则", 0.2F, builder));
                    this.getEnemy().forEach(o -> RuleUtil.addRule(o, OrganismPropertiesEnum.BEHAVIOR_SPEED, "零度法则", -0.2F, builder));
                    battleMsg.add(builder.toString());
                }

                @Override
                public void processIfHit(BattleDTO tar, SkillEnum skillEnum, AtomicLong damage, StringBuilder builder) {
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.MAGIC_ATK, "", 0.3F), 1, builder);
                }

                @Override
                public void processIfOurHit(BattleDTO tar, SkillEnum skillEnum, AtomicLong damage, StringBuilder builder) {
                    List<BattleBuffDTO> buffList = tar.getBuffMap().get(BuffTypeEnum.PAUSE);
                    if (CollectionUtils.isNotEmpty(buffList) &&
                            buffList.stream().anyMatch(buff -> "冻结".equals(buff.getBuffDTO().getBuffName()))) {
                        damage.set(Math.round(damage.get() * 1.5));
                    }
                }

                @Override
                public void processIfNotHit(BattleDTO tar, SkillEnum skillEnum, StringBuilder builder) {
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.HIT, "冰海沉星", 0.3F), 1, builder);
                    this.getEnemy().forEach(enemy -> {
                        BuffUtil.addBuff(enemy, new BuffDTO(BuffTypeEnum.PAUSE, "冻结", 0.3F, true), 1, builder);
                        BuffUtil.addBuff(enemy, new BuffDTO(BuffTypeEnum.SPEED, "冰海沉星", -0.1F), 2, builder);
                    });
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
