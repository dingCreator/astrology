package com.dingCreator.astrology.enums.skill;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.dto.BattleBuffDTO;
import com.dingCreator.astrology.dto.BattleDTO;
import com.dingCreator.astrology.dto.BuffDTO;
import com.dingCreator.astrology.dto.GiveBuffDTO;
import com.dingCreator.astrology.dto.organism.OrganismDTO;
import com.dingCreator.astrology.dto.organism.OrganismInfoDTO;
import com.dingCreator.astrology.dto.skill.SkillEffectDTO;
import com.dingCreator.astrology.enums.BuffOverrideStrategyEnum;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import com.dingCreator.astrology.enums.OrganismPropertiesEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.util.*;
import com.dingCreator.astrology.util.template.ExtraBattleProcessTemplate;
import com.dingCreator.astrology.util.template.ThisBehaviorExtraBattleProcessTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/4
 */
@Getter
@AllArgsConstructor
@Accessors(chain = true)
public enum SkillEnum {
    /**
     * 技能列表
     */
    SKILL_1(1L, "御剑术",
            "修真者使用仙剑进行攻击，对敌方造成90%物理伤害",
            JobEnum.XIU_ZHEN.getJobCode(),
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.9F)),

    SKILL_2(2L, "顺劈",
            "死地武士挥动武士刀进行劈砍，对敌方造成120%物理伤害",
            JobEnum.SI_DI_WU_SHI.getJobCode(),
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.2F)),

    SKILL_3(3L, "唤星术",
            "星星具有蛊惑人的力量，星术师发动唤星术造成100%法术伤害的同时，有10%概率降低敌方50%命中值，持续一回合",
            JobEnum.MAGICIAN.getJobCode(),
            new SkillEffectDTO(
                    SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 1F,
                    new GiveBuffDTO(BuffTypeEnum.HIT, "失神", -0.5F, 1, 0.1F, true)
            )
    ),

    SKILL_4(4L, "速射",
            "枪炮师引动枪弹进行四次射击，每次射击造成50%物理伤害",
            JobEnum.GUN.getJobCode(),
            Collections.nCopies(4, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F))
    ),

    SKILL_5(5L, "汲魂之隙",
            "造成100%物理伤害同时从中汲取生命力，使此攻击附带12%物理吸血，每次攻击造成伤害时使敌方防御降低1%持续一回合，" +
                    "每使用一次该防御降低效果增加1%，最多不超过50%。",
            JobEnum.EVIL.getJobCode(), new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F
    ), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void ifHit(BattleDTO from, BattleDTO tar,
                          List<BattleDTO> our, List<BattleDTO> enemy,
                          AtomicLong damage, boolean critical, StringBuilder builder) {
            int cnt = from.getMarkMap().getOrDefault("汲魂之隙", 0) + 1;
            cnt = Math.min(cnt, 50);
            from.getMarkMap().put("汲魂之隙", cnt);
            BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.DEF, "", -0.01F * cnt), 1, builder);
            BattleUtil.doHealing(from, Math.round(damage.get() * 0.12F), builder);
        }
    }),

    SKILL_6(6L, "芬芳",
            "小嘴抹蜜口吐芬芳，对敌方全体造成80%物理伤害并且有10%概率嘲讽对方1回合",
            JobEnum.CHEATER.getJobCode(),
            Arrays.asList(
                    new SkillEffectDTO(SkillTargetEnum.ALL_ENEMY, DamageTypeEnum.ATK, 0.8F),
                    new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
                            new GiveBuffDTO(BuffTypeEnum.TAUNT, "", 1, 0.1F)
                    )
            )
    ),

    SKILL_7(7L, "坠星珠",
            "星术师与敌人对战中领悟的伤害性技能，造成600%法术伤害。 蓝耗35",
            JobEnum.MAGICIAN.getJobCode(), 35L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 6F)
    ),

    SKILL_8(8L, "无罔归一",
            "修真者领悟御剑归一之境，能够对敌人造成450%物理伤害，蓝耗15",
            JobEnum.XIU_ZHEN.getJobCode(), 15L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 4.5F)
    ),

    SKILL_9(9L, "星之宠儿",
            "星术师作为星术界天才中的天才，他天生拥有对星术的超强控制力，每场战斗开始时能够免疫10%法术伤害，同时提高自身15%法强，持续整场战斗",
            JobEnum.MAGICIAN.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(List<String> battleMsg) {
            if (Objects.nonNull(this.getFrom())) {
                StringBuilder builder = new StringBuilder("※" + getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                        .append("的被动技能【").append(SKILL_9.getName()).append("】被触发");
                BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.MAGIC_ATK, "", 0.15F), 1000, builder);
                BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.MAGIC_DAMAGE, "", -0.1F), 1000, builder);
                battleMsg.add(builder.toString());
            }
        }
    }),

    SKILL_10(10L, "霸刀罡气",
            "霸刀门独门绝技，能透过防御直达实体，增加自身25%穿透与25%法穿",
            "All", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(List<String> battleMsg) {
            StringBuilder builder = new StringBuilder("※" + getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动技能【").append(SKILL_10.getName()).append("】被触发");
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.PENETRATE, "", 0.25F), 1000, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.MAGIC_DAMAGE, "", 0.25F), 1000, builder);
            battleMsg.add(builder.toString());
        }
    }),

    SKILL_11(11L, "普通攻击（物）",
            "造成110%物理伤害",
            "All", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.1F)
    ),

    SKILL_12(12L, "地裂斩",
            "造成350%物理伤害",
            "All", 30L, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 3.5F)
    ),

    SKILL_13(13L, "鬼王游行",
            "化身鬼魅遨游战场，提高自身30%速度持续2回合，对敌方造成1000%物理伤害",
            "All", 60L,
            Arrays.asList(
                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 10F),
                    new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F, Collections.singletonList(
                            new GiveBuffDTO(BuffTypeEnum.SPEED, "", 2, 0.3F)
                    ))
            )
    ),

    SKILL_14(14L, "音噬",
            "攻击命中敌人时有30%概率使敌方进入回音状态，持续2回合。回音：造成伤害时受到造成伤害量15%的真实伤害（反伤）",
            JobEnum.MAGICIAN.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage, boolean critical, StringBuilder builder) {
            if (RandomUtil.isHit(0.3F)) {
                BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.REFLECT_DAMAGE, "回音", 0.15F, true), 2, builder);
            }
        }
    }),

    SKILL_15(15L, "音敕",
            "造成110%法术伤害",
            "All", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 1.1F)
    ),

    SKILL_16(16L, "诡惑迷音",
            "造成400%法术伤害并使敌方眩晕持续1回合",
            "All", 30L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 4F, Collections.singletonList(
                    new GiveBuffDTO(BuffTypeEnum.PAUSE, "眩晕", 1, true)
            ))
    ),

    SKILL_17(17L, "回音圣域",
            "对敌方全体造成600%法术伤害，并使敌方全体进入回音状态持续3回合。回音：造成伤害时受到造成伤害量15%的真实伤害（反伤）",
            "All", 60L,
            new SkillEffectDTO(SkillTargetEnum.ALL_ENEMY, DamageTypeEnum.MAGIC, 6F, Collections.singletonList(
                    new GiveBuffDTO(BuffTypeEnum.REFLECT_DAMAGE, "回音", 3)
            ))
    ),

    SKILL_18(18L, "普通攻击（法）",
            "造成100%法术伤害",
            "All", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 1F)
    ),

    SKILL_19(19L, "汲咀之源",
            "造成200%法术伤害，技能命中时降低敌方10%法强，同时提高自身10%法强持续1回合",
            "All", 25L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 2F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleDTO from, BattleDTO tar,
                                  List<BattleDTO> our, List<BattleDTO> enemy,
                                  AtomicLong damage, boolean critical, StringBuilder builder) {
                    BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.MAGIC_ATK, "", 0.1F), 1, builder);
                    BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.MAGIC_ATK, "", -0.1F), 1, builder);
                }
            }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_20(20L, "源星盾",
            "星术师操控星辰力形成护罩缠绕自身，能够抵挡敌方一次伤害，同时提高自身20%法强和20%命中持续3回合",
            JobEnum.MAGICIAN.getJobCode(), 60L,
            new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.MAGIC, 0F,
                    Arrays.asList(
                            new GiveBuffDTO(BuffTypeEnum.MAGIC_ATK, "", 0.2F, 3),
                            new GiveBuffDTO(BuffTypeEnum.HIT, "", 0.2F, 3)
                    )),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleDTO from, BattleDTO tar,
                                  List<BattleDTO> our, List<BattleDTO> enemy,
                                  AtomicLong damage, boolean critical, StringBuilder builder) {
                    int cnt = from.getMarkMap().getOrDefault("源星盾", 0) + 1;
                    from.getMarkMap().put("源星盾", cnt);
                }
            },
            new ExtraBattleProcessTemplate() {
                @Override
                public boolean canEffect(BattleDTO tar, SkillEffectDTO skillEffect, StringBuilder builder) {
                    if (tar.equals(this.getFrom())) {
                        int cnt = tar.getMarkMap().getOrDefault("源星盾", 0);
                        if (cnt > 0 && skillEffect.getDamageRate() > 0) {
                            tar.getMarkMap().put("源星盾", cnt - 1);
                            builder.append("，").append(tar.getOrganismInfoDTO().getOrganismDTO().getName())
                                    .append("的【源星盾】抵消此次伤害，剩余次数：").append(cnt - 1);
                            return false;
                        }
                    }
                    return true;
                }
            }
    ),

    SKILL_21(21L, "侵蚀雾霭",
            "死地武士周身缠绕着经久不散的雾霭，他的攻击会附加（10%攻击力*技能倍率）的真实伤害。并附加雾霭异常。" +
                    "雾霭异常效果：被死地武士击中的单位会受到雾霭的侵蚀而降低10%速度持续一回合",
            JobEnum.SI_DI_WU_SHI.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect,
                                      AtomicLong damage, boolean critical, StringBuilder builder) {
            if (skillEffect.getDamageRate() > 0) {
                OrganismDTO fromOrganism = this.getFrom().getOrganismInfoDTO().getOrganismDTO();
                BattleUtil.doRealDamage(tar, Math.round(0.1 * fromOrganism.getAtk() * skillEffect.getDamageRate()), builder);
                BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.SPEED, "雾霭", -0.1F, true), 1, builder);
            }
        }
    }),

    SKILL_22(22L, "伪神之眼",
            "枪炮师拥有未元之都的尖端科技产物——伪神之眼，能够将视域扩大到方圆百公里，每次攻击命中时都会为枪炮师提供2%穿透和5%命中提升效果持续两回合，" +
                    "可叠加，最高不得超过20%和50%",
            JobEnum.GUN.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect,
                                      AtomicLong damage, boolean critical, StringBuilder builder) {
            Map<BuffTypeEnum, List<BattleBuffDTO>> buffMap = this.getFrom().getBuffMap();
            if (buffMap.containsKey(BuffTypeEnum.PENETRATE)) {
                BigDecimal rate = buffMap.get(BuffTypeEnum.PENETRATE).stream()
                        .map(BattleBuffDTO::getBuffDTO)
                        .filter(buffDTO -> "伪神之眼".equals(buffDTO.getBuffName()))
                        .map(BuffDTO::getRate).reduce((rate1, rate2) -> rate2.add(rate1)).orElse(BigDecimal.ZERO);
                if (rate.compareTo(BigDecimal.valueOf(0.2)) <= 0) {
                    if (!builder.toString().contains("伪神之眼")) {
                        builder.append("，").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                                .append("的被动技能【伪神之眼】被触发");
                    }
                    BuffDTO buffDTO = new BuffDTO(BuffTypeEnum.PENETRATE, "伪神之眼", 0.02F,
                            BuffOverrideStrategyEnum.NONINTERFERENCE);
                    BuffUtil.addBuff(this.getFrom(), buffDTO, 2, builder);
                }
            }
            if (buffMap.containsKey(BuffTypeEnum.HIT)) {
                BigDecimal rate = buffMap.get(BuffTypeEnum.HIT).stream()
                        .map(BattleBuffDTO::getBuffDTO)
                        .filter(buffDTO -> "伪神之眼".equals(buffDTO.getBuffName()))
                        .map(BuffDTO::getRate).reduce((rate1, rate2) -> rate2.add(rate1)).orElse(BigDecimal.ZERO);
                if (rate.compareTo(BigDecimal.valueOf(0.5)) <= 0) {
                    if (!builder.toString().contains("伪神之眼")) {
                        builder.append("，").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                                .append("的被动技能【伪神之眼】被触发");
                    }
                    BuffDTO buffDTO = new BuffDTO(BuffTypeEnum.HIT, "伪神之眼", 0.05F,
                            BuffOverrideStrategyEnum.NONINTERFERENCE);
                    BuffUtil.addBuff(this.getFrom(), buffDTO, 2, builder);
                }
            }
        }
    }),

    SKILL_23(23L, "怯勇",
            "同低于等于自身境界的对手（包括BOSS）战斗时额外获得20%减伤和24%攻击加成，若低于敌方等级则额外受到15%伤害",
            JobEnum.EVIL.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(List<String> battleMsg) {
            StringBuilder builder = new StringBuilder("※" + getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动技能【怯勇】被触发");
            int fromRank = getFrom().getOrganismInfoDTO().getOrganismDTO().getRank();
            int highestTarRank = getEnemy().stream()
                    .map(BattleDTO::getOrganismInfoDTO)
                    .map(OrganismInfoDTO::getOrganismDTO)
                    .mapToInt(OrganismDTO::getRank).max().orElse(0);
            if (fromRank >= highestTarRank) {
                BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.DAMAGE, "", -0.2F), 1000, builder);
                BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "", 0.24F), 1000, builder);
            } else {
                BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.DAMAGE, "", 0.15F), 1000, builder);
            }
            battleMsg.add(builder.toString());
        }
    }),

    SKILL_24(24L, "剑魂",
            "修真者拥有极高的剑道天赋，每次出剑都会更加凌厉。其每次出手前使自身攻击力提高5%",
            JobEnum.XIU_ZHEN.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMyBehavior(BattleDTO tar, StringBuilder builder) {
            builder.append(",").append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动技能【剑魂】被触发");
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "", 0.05F), 1000, builder);
        }
    }),

    SKILL_25(25L, "凌锋踏步",
            "修真者引动自身气旋，提高自身50%速度和50%攻击力持续3回合",
            JobEnum.XIU_ZHEN.getJobCode(), 90L, new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
            Arrays.asList(
                    new GiveBuffDTO(BuffTypeEnum.SPEED, "", 0.5F, 3),
                    new GiveBuffDTO(BuffTypeEnum.ATK, "", 0.5F, 3)
            ))
    ),

    SKILL_26(26L, "气旋",
            "修真者每回合回复自身蓝量的5%",
            JobEnum.XIU_ZHEN.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMyRound(List<String> battleMsg) {
            StringBuilder builder = new StringBuilder("※" + getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动技能【气旋】被触发");
            BattleUtil.doMpRecover(this.getFrom(),
                    Math.round(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getMaxMpWithAddition() * 0.05F), builder);
        }
    }),

    SKILL_27(27L, "羽破千翎",
            "修真者进行快速连续的四段斩击，每段造成150%物理伤害",
            JobEnum.XIU_ZHEN.getJobCode(), 30L,
            Collections.nCopies(4, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.5F))
    ),

    SKILL_28(28L, "煜塞天河",
            "修真者感悟天地之力，能够使用天地之力对敌方进行封锁，使进入眩晕异常2回合",
            JobEnum.XIU_ZHEN.getJobCode(), 80L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0F,
                    new GiveBuffDTO(BuffTypeEnum.PAUSE, "眩晕", 2, true)
            )
    ),

    SKILL_29(29L, "万里无垠",
            "修真者突破极巅领悟的招式，对敌人造成1200%物理伤害。展开无垠领域，己方单位全体获得25%攻击与25%速度加成，" +
                    "敌方全体单位降低10%防御与10%速度。无垠领域展开持续5回合",
            JobEnum.XIU_ZHEN.getJobCode(), 120L,
            Arrays.asList(
                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 12F, Arrays.asList(
                            new GiveBuffDTO(BuffTypeEnum.DEF, "", -0.1F, 5),
                            new GiveBuffDTO(BuffTypeEnum.SPEED, "", -0.1F, 5)
                    )),
                    new SkillEffectDTO(SkillTargetEnum.ALL_OUR, DamageTypeEnum.ATK, 0F, Arrays.asList(
                            new GiveBuffDTO(BuffTypeEnum.ATK, "", 0.25F, 5),
                            new GiveBuffDTO(BuffTypeEnum.SPEED, "", 0.25F, 5)
                    ))
            )
    ),

    SKILL_30(30L, "狂暴烈斩",
            "死地武士进行猛烈斩击，造成600%物理伤害，并降低自身10%速度",
            JobEnum.SI_DI_WU_SHI.getJobCode(), 15L, Arrays.asList(
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 6F),
            new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
                    new GiveBuffDTO(BuffTypeEnum.SPEED, "", -0.1F, 1)
            )
    )),

    SKILL_31(31L, "蒙络坠影",
            "死地武士使驱使雾霭缠绕自身，增加自身20%防御力和30%法抗并可以反弹受到伤害的15%的真实伤害，持续3回合",
            JobEnum.SI_DI_WU_SHI.getJobCode(), 80L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 6F, Arrays.asList(
                    new GiveBuffDTO(BuffTypeEnum.DEF, "", 0.2F, 3),
                    new GiveBuffDTO(BuffTypeEnum.MAGIC_DEF, "", 0.3F, 3)
            ))
    ),

    SKILL_32(32L, "浴血神战",
            "造成800%物理伤害。装备此技能后，在战斗濒临死亡之际会爆发绝境的意志，受到致命伤害不会死亡，并回复自身全部血量和蓝量，" +
                    "但会降低自身60%攻击力持续整场战斗。每场战斗仅触发一次",
            JobEnum.SI_DI_WU_SHI.getJobCode(), 80L, Arrays.asList(
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 6F),
            new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
                    new GiveBuffDTO(BuffTypeEnum.ATK, "", -0.6F, 1000)
            )
    ), new ThisBehaviorExtraBattleProcessTemplate() {
    }, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMeDeath(BattleDTO from, BattleDTO tar, AtomicLong atomicDamage, StringBuilder builder) {
            atomicDamage.set(0L);
            builder.append("，☠").append(from.getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("受到致命伤害，技能【浴血神战】效果被触发，免疫此次伤害");
            BattleUtil.doHealing(from, from.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition(), builder);
            BattleUtil.doMpRecover(from, from.getOrganismInfoDTO().getOrganismDTO().getMaxMpWithAddition(), builder);
            BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.ATK, "", -0.6F), 1000, builder);
        }
    }),

    SKILL_33(33L, "修罗法身",
            "死地武士爆发全部雾霭的力量，开启狂暴效果，狂暴开启时清除自身减益和异常，同时5回合内不受异常和buff类弱化效果影响。" +
                    "5回合内提升自身30%血量上限和20%防御、法抗和25%攻击，同时将被动技能的真实伤害效果提高到50%持续5回合。" +
                    "5回合内每回合回复自身10%血量",
            // todo 限定回合的血量上限变化
            JobEnum.SI_DI_WU_SHI.getJobCode(), 120L,
            new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F, Arrays.asList(
                    new GiveBuffDTO(BuffTypeEnum.ATK, "", 0.25F, 5),
                    new GiveBuffDTO(BuffTypeEnum.DEF, "", 0.2F, 5),
                    new GiveBuffDTO(BuffTypeEnum.MAGIC_DEF, "", 0.2F, 5),
                    new GiveBuffDTO(BuffTypeEnum.IMMUNITY, "修罗法身", 5)
            )), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void afterEffect(BattleDTO from, BattleDTO tar, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {
            BuffUtil.clearAbnormalBuff(from, builder);
            BuffUtil.clearInactiveBuff(from, builder);
        }
    }),

    SKILL_34(34L, "九龙归允——太初法则",
            "每回合随机触发九龙之一。\n" +
                    "鸣翔之龙：提升自身30%速度持续一回合。\n" +
                    "破云之龙：提升自身30%攻击持续一回合。\n" +
                    "镇世之龙：提升自身30%防御和法抗持续一回合。\n" +
                    "惊霆之龙：提升自身30%破甲持续一回合。\n" +
                    "山海之龙：提升自身30%命中持续一回合。\n" +
                    "圣洁之龙：解除自身异常状态和弱化状态。\n" +
                    "彼岸之龙：受到致死伤害时复活，每场战斗限一次，复活后可继续触发彼岸之龙但不会生效。\n" +
                    "湮灭之龙：立即对所有敌方造成一次相当于自身50%攻击力的真实伤害。\n" +
                    "自然之龙：回复自身10%生命。",
            "All", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMyRound(List<String> battleMsg) {
            StringBuilder builder = new StringBuilder("※" + getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动技能【九龙归允——太初法则】被触发");
            int mode;
            if (this.getFrom().getMarkMap().containsKey("太初神誓")) {
                mode = (1 << 8) + (1 << 7) + (1 << 6) + (1 << 5) + (1 << 4) + (1 << 3) + (1 << 2) + (1 << 1) + 1;
            } else {
                mode = 1 << RandomUtil.rangeIntRandom(9);
            }
            for (int i = 0; i < 9; i++) {
                if ((mode >> i) % 2 == 1) {
                    switch (i) {
                        case 0:
                            builder.append("，触发【鸣翔之龙】");
                            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.SPEED, "", 0.3F), 1, builder);
                            break;
                        case 1:
                            builder.append("，触发【破云之龙】");
                            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "", 0.3F), 1, builder);
                            break;
                        case 2:
                            builder.append("，触发【镇世之龙】");
                            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.DEF, "", 0.3F), 1, builder);
                            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.MAGIC_DEF, "", 0.3F), 1, builder);
                            break;
                        case 3:
                            builder.append("，触发【惊霆之龙】");
                            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.PENETRATE, "", 0.3F), 1, builder);
                            break;
                        case 4:
                            builder.append("，触发【山海之龙】");
                            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.HIT, "", 0.3F), 1, builder);
                            break;
                        case 5:
                            builder.append("，触发【圣洁之龙】");
                            BuffUtil.clearAbnormalBuff(this.getFrom(), builder);
                            break;
                        case 6:
                            builder.append("，触发【彼岸之龙】");
                            this.getFrom().getMarkMap().put("彼岸之龙", 1);
                            break;
                        case 7:
                            builder.append("，触发【湮灭之龙】");
                            long realDamage = Math.round(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getAtk() * 0.5F);
                            this.getEnemy().forEach(e -> BattleUtil.doRealDamage(e, realDamage, builder));
                            break;
                        case 8:
                            builder.append("，触发【自然之龙】");
                            long heal = Math.round(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition() * 0.1F);
                            BattleUtil.doHealing(this.getFrom(), heal, builder);
                            break;
                        default:
                            builder.append("技能使用失败");
                            break;
                    }
                }
            }
            battleMsg.add(builder.toString());
        }

        @Override
        public void beforeMeDeath(BattleDTO from, BattleDTO tar, AtomicLong atoDamage, StringBuilder builder) {
            Map<String, Integer> markMap = this.getFrom().getMarkMap();
            if (markMap.containsKey("彼岸之龙") && !markMap.containsKey("已触发彼岸之龙")) {
                atoDamage.set(0);
                markMap.put("已触发彼岸之龙", 1);
                markMap.remove("彼岸之龙");

                BattleDTO battleDTO = this.getFrom();
                OrganismDTO organismDTO = battleDTO.getOrganismInfoDTO().getOrganismDTO();
                builder.append("，☠").append(organismDTO.getName()).append("受到致命伤害，【彼岸之龙】效果被触发，复活");
                BattleUtil.doHealing(battleDTO, organismDTO.getMaxHpWithAddition(), builder);
                BattleUtil.doMpRecover(battleDTO, organismDTO.getMaxMpWithAddition(), builder);
                BuffUtil.clearAllBuff(battleDTO, builder);
            }
        }
    }),

    SKILL_35(35L, "九龙僭越",
            "对敌方造成九次60%的物理伤害，每段伤害命中时降低敌方2%防御，持续一回合",
            "All", 40L, Collections.nCopies(9,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.6F,
                    new GiveBuffDTO(BuffTypeEnum.DEF, "", -0.02F, 1)
            )
    )),

    SKILL_36(36L, "太初神誓",
            "全力催动祖龙血脉，释放太初法则，三回合内，每回合九龙效果全部生效",
            "All", 100L, new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterThisRound(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {
                    if (from.getMarkMap().containsKey("太初神誓")) {
                        int cnt = from.getMarkMap().get("太初神誓") - 1;
                        if (cnt < 0) {
                            from.getMarkMap().remove("太初神誓");
                        } else {
                            from.getMarkMap().put("太初神誓", cnt);
                        }
                    }
                }

                @Override
                public void ifHit(BattleDTO from, BattleDTO tar,
                                  List<BattleDTO> our, List<BattleDTO> enemy,
                                  AtomicLong damage, boolean critical, StringBuilder builder) {
                    from.getMarkMap().put("太初神誓", 3);
                }
            }
    ),

    SKILL_37(37L, "惘星叹",
            "星术师的星术变幻莫测能够扰乱敌方的神志，造成750%法术伤害，同时使敌方失神（降低敌方50%命中率）持续2回合，" +
                    "如果命中时敌方已经失神则额外造成眩晕效果持续1回合",
            JobEnum.MAGICIAN.getJobCode(), 100L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 7.5F,
                    new GiveBuffDTO(BuffTypeEnum.HIT, "失神", -0.5F, 2, true)
            ), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void ifHit(BattleDTO from, BattleDTO tar,
                          List<BattleDTO> our, List<BattleDTO> enemy,
                          AtomicLong damage, boolean critical, StringBuilder builder) {
            List<BattleBuffDTO> buffList = tar.getBuffMap().get(BuffTypeEnum.HIT);
            if (CollectionUtil.isNotEmpty(buffList)
                    && buffList.stream().anyMatch(buff -> "失神".equals(buff.getBuffDTO().getBuffName()))) {
                BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.PAUSE, "眩晕", 0L, true), 1, builder);
            }
        }
    }),

    SKILL_38(38L, "幻星神",
            "星术师操纵星术的能力出神入化，回复己方全体20%蓝量并提高自身50%速度持续2回合",
            JobEnum.MAGICIAN.getJobCode(), 150L,
            new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.MAGIC, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleDTO from, BattleDTO tar,
                                  List<BattleDTO> our, List<BattleDTO> enemy,
                                  AtomicLong damage, boolean critical, StringBuilder builder) {
                    our.forEach(o -> {
                        long maxMp = o.getOrganismInfoDTO().getOrganismDTO().getMaxMpWithAddition();
                        BattleUtil.doMpRecover(o, Math.round(0.2 * maxMp), builder);
                    });
                    BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.SPEED, "", 0.5F), 2, builder);
                }
            }
    ),

    SKILL_39(39L, "星坠之时·来自宇宙",
            "出手前提高自身命中999999999和15%法穿持续0回合，星术师施展的星术终极奥义，对敌方全体造成1800%法术伤害同时对敌方全体附加失神1回合",
            JobEnum.MAGICIAN.getJobCode(), 150L,
            new SkillEffectDTO(SkillTargetEnum.ALL_ENEMY, DamageTypeEnum.MAGIC, 18F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void beforeThisRound(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {
                    BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.HIT, "", 999999999L), 0, builder);
                    BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.MAGIC_PENETRATE, "", 0.15F), 0, builder);
                }

                @Override
                public void ifHit(BattleDTO from, BattleDTO tar,
                                  List<BattleDTO> our, List<BattleDTO> enemy,
                                  AtomicLong damage, boolean critical, StringBuilder builder) {
                    BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.HIT, "失神", -0.5F), 1, builder);
                }
            }
    ),

    SKILL_40(40L, "热核弹头",
            "枪炮师装载热核弹头，出手前提高自身15%穿透0回合，并造成450%物理伤害",
            JobEnum.GUN.getJobCode(), 25L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 4.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void beforeThisRound(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {
                    BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.PENETRATE, "", 0.15F), 0, builder);
                }
            }
    ),

    SKILL_41(41L, "虚灵魅影",
            "提升自身30%速度，40%闪避，持续三回合",
            JobEnum.GUN.getJobCode(), 25L,
            new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
                    Arrays.asList(
                            new GiveBuffDTO(BuffTypeEnum.SPEED, "", 0.3F, 3),
                            new GiveBuffDTO(BuffTypeEnum.DODGE, "", 0.4F, 3)
                    )
            )
    ),

    SKILL_42(42L, "斩魂",
            "造成500%物理伤害，命中后降低敌方当前最大血量5%的血量上限",
            JobEnum.EVIL.getJobCode(), 45L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 5F,
                    Arrays.asList(
                            new GiveBuffDTO(BuffTypeEnum.SPEED, "", 0.3F, 3),
                            new GiveBuffDTO(BuffTypeEnum.DODGE, "", 0.4F, 3)
                    )
            )
    ),

    SKILL_43(43L, "血戮",
            "提升自身40%攻击，30%吸血，持续4回合",
            JobEnum.EVIL.getJobCode(), 50L,
            new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
                    Arrays.asList(
                            new GiveBuffDTO(BuffTypeEnum.ATK, "", 0.4F, 4),
                            new GiveBuffDTO(BuffTypeEnum.LIFE_STEAL, "", 0.3F, 4)
                    )
            )
    ),

    SKILL_44(44L, "贪慑",
            "提升自身50%吸血持续1回合，震慑敌方，命中后使敌方眩晕1回合，对敌方造成800%物理伤害",
            JobEnum.EVIL.getJobCode(), 70L, Arrays.asList(
            new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
                    new GiveBuffDTO(BuffTypeEnum.LIFE_STEAL, "", 0.5F, 1)
            ),
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 8F,
                    new GiveBuffDTO(BuffTypeEnum.PAUSE, "眩晕", 0.5F, 1)
            )
    )),

    SKILL_45(45L, "神恸",
            "清除自身所有异常状态与弱化效果。受到致死攻击时免除死亡并回复血量上限60%的血量，每场战斗限一次",
            JobEnum.EVIL.getJobCode(), 70L, new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleDTO from, BattleDTO tar,
                                  List<BattleDTO> our, List<BattleDTO> enemy,
                                  AtomicLong damage, boolean critical, StringBuilder builder) {
                    BuffUtil.clearAbnormalBuff(from, builder);
                    BuffUtil.clearInactiveBuff(from, builder);
                }
            },
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeMeDeath(BattleDTO from, BattleDTO tar, AtomicLong atoDamage, StringBuilder builder) {
                    if (!this.getFrom().getMarkMap().containsKey("神恸")) {
                        this.getFrom().getMarkMap().put("神恸", 1);
                        atoDamage.set(0L);
                        OrganismDTO o = this.getFrom().getOrganismInfoDTO().getOrganismDTO();
                        BattleUtil.doHealing(this.getFrom(), Math.round(0.6 * o.getMaxHpWithAddition()), builder);
                    }
                }
            }
    ),

    SKILL_46(46L, "灾祭",
            "自身行动20回合后战斗仍未结束，降低自身70%血量上限，并且提升200%攻击与40%吸血",
            JobEnum.EVIL.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMyRound(List<String> battleMsg) {
            if (this.getFrom().getRound() >= 20 && !this.getFrom().getMarkMap().containsKey("灾祭")) {
                this.getFrom().getMarkMap().put("灾祭", 1);
                OrganismDTO organism = this.getFrom().getOrganismInfoDTO().getOrganismDTO();
                StringBuilder builder = new StringBuilder("※").append(organism.getName())
                        .append("的被动技能【灾祭】被触发");
                long newMaxHp = Math.round(organism.getMaxHpWithAddition() * 0.7);
                builder.append("，最大血量减少").append(organism.getMaxHpWithAddition() - newMaxHp).append("点");
                organism.setMaxHpWithAddition(newMaxHp);
                // 重算超过上限的血量
                organism.setHpWithAddition(organism.getHpWithAddition());
                BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "", 2F), 1000, builder);
                BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.LIFE_STEAL, "", 0.4F), 1000, builder);
                battleMsg.add(builder.toString());
            }
        }
    }),


    SKILL_1000(1000L, "雷击",
            "天劫的基础手段，造成100%物理伤害",
            "None", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F)
    ),

    SKILL_1001(1001L, "天雷化身-剑",
            "天劫化身为剑，造成50%物理伤害，并提升5%攻击，持续5回合",
            "None", 10L,
            Arrays.asList(
                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.8F),
                    new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
                            new GiveBuffDTO(BuffTypeEnum.ATK, "", 0.05F, 5, 1F)
                    )
            )
    ),

    SKILL_1002(1002L, "天威",
            "天劫释放天威，降低对方10%攻击和10%法强，持续5回合",
            "None", 20L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0F,
                    Arrays.asList(
                            new GiveBuffDTO(BuffTypeEnum.ATK, "", -0.1F, 5),
                            new GiveBuffDTO(BuffTypeEnum.MAGIC_ATK, "", -0.1F, 5)
                    )
            )
    ),

    SKILL_1003(1003L, "五雷轰顶",
            "天劫释放五道天雷，分别造成150%，180%，220%，300%，350%物理伤害",
            "None", 100L,
            Arrays.asList(
                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.5F),
                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.8F),
                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 2.2F),
                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 3F),
                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 3.5F)
            )
    ),

    SKILL_1004(1004L, "雷霆万钧",
            "雷霆之所击，无不摧折者；万钧之所压，无不糜灭者。造成自身攻击1000%的真实伤害，并降低对方10%的攻击、法强、防御、法抗、速度、" +
                    "命中、闪避、暴击、抗暴、爆伤、爆免，持续5回合",
            "None", 1000L,
            Collections.singletonList(
                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.SPECIAL, 10F,
                            Arrays.asList(
                                    new GiveBuffDTO(BuffTypeEnum.ATK, "", -0.1F, 5, 1F),
                                    new GiveBuffDTO(BuffTypeEnum.DEF, "", -0.1F, 5, 1F),
                                    new GiveBuffDTO(BuffTypeEnum.MAGIC_ATK, "", -0.1F, 5, 1F),
                                    new GiveBuffDTO(BuffTypeEnum.MAGIC_DEF, "", -0.1F, 5, 1F),
                                    new GiveBuffDTO(BuffTypeEnum.SPEED, "", -0.1F, 5, 1F),
                                    new GiveBuffDTO(BuffTypeEnum.HIT, "", -0.1F, 5, 1F),
                                    new GiveBuffDTO(BuffTypeEnum.DODGE, "", -0.1F, 5, 1F),
                                    new GiveBuffDTO(BuffTypeEnum.CRITICAL, "", -0.1F, 5, 1F),
                                    new GiveBuffDTO(BuffTypeEnum.CRITICAL_REDUCTION, "", -0.1F, 5, 1F),
                                    new GiveBuffDTO(BuffTypeEnum.CRITICAL_DAMAGE, "", -0.1F, 5, 1F),
                                    new GiveBuffDTO(BuffTypeEnum.CRITICAL_DAMAGE_REDUCTION, "", -0.1F, 5, 1F)
                            )
                    )
            )
    ),

    SKILL_1005(1005L, "天雷化身-盾",
            "天劫化身为盾，造成50%物理伤害，并提升10%防御和10%法抗，持续5回合",
            "None", 10L,
            Arrays.asList(
                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F),
                    new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
                            Arrays.asList(
                                    new GiveBuffDTO(BuffTypeEnum.DEF, "", 0.1F, 5),
                                    new GiveBuffDTO(BuffTypeEnum.DEF, "", 0.1F, 5)
                            )
                    )
            )
    ),

    SKILL_1008(1008L, "虫殖",
            "造成伤害后获得一层虫殖印记，每层虫殖印记将为其增加2%双攻 ，5%吸血，虫殖印记数量最大为10",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void processIfHit(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage,
                                 boolean critical, StringBuilder builder) {
            if (damage.get() > 0) {
                Map<String, Integer> markMap = this.getFrom().getMarkMap();
                int cnt = markMap.getOrDefault("虫殖", 0);
                int newCnt = cnt >= 10 ? cnt : cnt + 1;
                markMap.put("虫殖", newCnt);

                BuffDTO atkBuff = new BuffDTO(BuffTypeEnum.ATK, "虫殖", 0.02F * newCnt);
                BuffDTO magicAtkBuff = new BuffDTO(BuffTypeEnum.MAGIC_ATK, "虫殖", 0.02F * newCnt);
                BuffDTO lifeStealBuff = new BuffDTO(BuffTypeEnum.LIFE_STEAL, "虫殖", 0.05F * newCnt);

                BuffUtil.addBuff(this.getFrom(), atkBuff, 1000, builder);
                BuffUtil.addBuff(this.getFrom(), magicAtkBuff, 1000, builder);
                BuffUtil.addBuff(this.getFrom(), lifeStealBuff, 1000, builder);
            }
        }

        @Override
        public Integer getPriority() {
            return 1000;
        }
    }),

    SKILL_1009(1009L, "虫噬",
            "对敌方造成（50%+10%虫殖印记）物理攻击伤害",
            "None", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public BigDecimal changeDamageRate(BattleDTO from, BattleDTO tar,
                                                   List<BattleDTO> our, List<BattleDTO> enemy,
                                                   BigDecimal damageRate, StringBuilder builder) {
                    int cnt = from.getMarkMap().getOrDefault("虫殖", 0);
                    return damageRate.add(BigDecimal.valueOf(0.1).multiply(BigDecimal.valueOf(cnt)));
                }
            }
    ),

    SKILL_1010(1010L, "蝗灭",
            "消耗自身15%生命值，造成（300%+50%虫殖印记）物理伤害。当前回合结束时，虫殖数量减半",
            "None", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 3F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void beforeEffect(BattleDTO from, BattleDTO tar,
                                         List<BattleDTO> our, List<BattleDTO> enemy,
                                         StringBuilder builder) {
                    OrganismDTO thisOrganism = from.getOrganismInfoDTO().getOrganismDTO();
                    long newHp = Math.round(thisOrganism.getHpWithAddition() * 0.85);
                    builder.append("，").append(from.getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("生命值减少了").append(thisOrganism.getHpWithAddition() - newHp);
                    thisOrganism.setHpWithAddition(newHp);
                }

                @Override
                public BigDecimal changeDamageRate(BattleDTO from, BattleDTO tar,
                                                   List<BattleDTO> our, List<BattleDTO> enemy,
                                                   BigDecimal damageRate, StringBuilder builder) {
                    int cnt = from.getMarkMap().getOrDefault("虫殖", 0);
                    return damageRate.add(BigDecimal.valueOf(0.5).multiply(BigDecimal.valueOf(cnt)));
                }

                @Override
                public void afterEffect(BattleDTO from, BattleDTO tar,
                                        List<BattleDTO> our, List<BattleDTO> enemy,
                                        StringBuilder builder) {
                    int cnt = from.getMarkMap().getOrDefault("虫殖", 0);
                    int newCnt = cnt / 2;
                    builder.append("，").append(from.getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的<虫殖>数量减少至").append(newCnt);
                    from.getMarkMap().put("虫殖", newCnt);
                }
            }
    ),

    SKILL_1011(1011L, "摩诃无量（残）",
            "战斗开始时，天无涯攻击力提升30%，持续三回合",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(List<String> battleMsg) {
            String msg = "※" + this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName() + "的被动技能【摩诃无量（残）】被触发";
            StringBuilder builder = new StringBuilder(msg);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "摩诃无量", 0.3F), 3, builder);
            battleMsg.add(builder.toString());
        }
    }),

    SKILL_1012(1012L, "斩",
            "造成110%物理伤害",
            "None", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.1F)
    ),

    SKILL_1013(1013L, "星术·珠明",
            "对敌方造成80%物理伤害",
            "None", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.8F)
    ),

    SKILL_1014(1014L, "星术·魂养",
            "使用时回复自身30%蓝量",
            "None", 300L, new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleDTO from, BattleDTO tar,
                                  List<BattleDTO> our, List<BattleDTO> enemy,
                                  AtomicLong damage, boolean critical, StringBuilder builder) {
                    OrganismDTO fromOrganismDTO = from.getOrganismInfoDTO().getOrganismDTO();
                    long oldMp = fromOrganismDTO.getMpWithAddition();
                    long newMp = BigDecimal.valueOf(oldMp).multiply(BigDecimal.valueOf(1.3)).longValue();
                    newMp = newMp > fromOrganismDTO.getMaxMpWithAddition() ? fromOrganismDTO.getMaxMpWithAddition() : newMp;
                    builder.append("，").append(fromOrganismDTO.getName()).append("蓝量回复").append(newMp - oldMp).append("点");
                    fromOrganismDTO.setMpWithAddition(newMp);
                }
            }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_1015(1015L, "星术·神养",
            "使用时回复自身130%法强点血量",
            "None", 1000L, new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleDTO from, BattleDTO tar,
                                  List<BattleDTO> our, List<BattleDTO> enemy,
                                  AtomicLong damage, boolean critical, StringBuilder builder) {
                    OrganismDTO fromOrganismDTO = from.getOrganismInfoDTO().getOrganismDTO();
                    long magicAtk = BattleUtil.getLongProperty(fromOrganismDTO.getMagicAtk(), BuffTypeEnum.MAGIC_ATK.getName(), from);
                    BattleUtil.doHealing(from, Math.round(magicAtk * 1.3F), builder);
                }
            }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_1016(1016L, "星术·嗔魂",
            "提高自身20%吸血持续2回合",
            "None", 300L, new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
            new GiveBuffDTO(BuffTypeEnum.LIFE_STEAL, "", 0.2F, 2))
    ),

    SKILL_1017(1017L, "星术·灭神",
            "对敌方造成1550%物理伤害",
            "None", 1000L, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 15.5F)
    ),

    SKILL_1018(1018L, "斫霜",
            "攻击造成伤害后50%概率降低敌方30%速度持续一回合。该效果未触发则降低敌方2%血量上限。",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void processIfHit(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage,
                                 boolean critical, StringBuilder builder) {
            if (damage.get() > 0) {
                boolean effect;
                if (this.getFrom().getMarkMap().containsKey(SKILL_1021.getName() + "-斫霜")) {
                    effect = true;
                    this.getFrom().getMarkMap().remove(SKILL_1021.getName() + "-斫霜");
                } else {
                    effect = RandomUtil.isHit(0.5F);
                }
                if (effect) {
                    BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.SPEED, "斫霜", -0.3F), 1, builder);
                } else {
                    OrganismDTO tarOrganism = tar.getOrganismInfoDTO().getOrganismDTO();
                    tarOrganism.setMaxHpWithAddition(BigDecimalUtil.multiply(tarOrganism.getMaxHpWithAddition(), 0.98F));
                    if (tarOrganism.getHpWithAddition() > tarOrganism.getMaxHpWithAddition()) {
                        tarOrganism.setHpWithAddition(tarOrganism.getMaxHpWithAddition());
                    }
                    builder.append("，").append(tarOrganism.getName()).append("血量上限-2%");
                }
            }
        }
    }),

    SKILL_1019(1019L, "雪剜",
            "造成80%物理+30%法术伤害",
            "None", Arrays.asList(
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.8F),
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 0.3F)
    )),

    SKILL_1020(1020L, "莹雪化谢",
            "造成300%法术+150%物理伤害，若造成伤害后对敌方造成“斫霜”的速度弱化状态，则在回合结束时使敌方冻结持续一回合",
            "None", 50L, Arrays.asList(
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.5F),
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 3F)
    ), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void ifHit(BattleDTO from, BattleDTO tar,
                          List<BattleDTO> our, List<BattleDTO> enemy,
                          AtomicLong damage, boolean critical, StringBuilder builder) {
            if (damage.get() > 0) {
                List<BattleBuffDTO> buffList = tar.getBuffMap().get(BuffTypeEnum.SPEED);
                if (CollectionUtil.isNotEmpty(buffList)
                        && buffList.stream().anyMatch(buff -> "斫霜".equals(buff.getBuffDTO().getBuffName()))) {
                    tar.getMarkMap().put("莹雪化谢-斫霜", 1);
                }
            }
        }

        @Override
        public void afterThisRound(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder
                builder) {
            enemy.stream().filter(e -> e.getMarkMap().containsKey("莹雪化谢-斫霜")).forEach(e -> {
                e.getMarkMap().remove("莹雪化谢-斫霜");
                BuffUtil.addBuff(e, new BuffDTO(BuffTypeEnum.PAUSE, "冻结", 0L, true), 1, builder);
            });
        }
    }),

    SKILL_1021(1021L, "精铁寒利",
            "提升自身30%攻击30%法强持续三回合，下一次攻击造成伤害后的斫霜的速度降低效果必定触发",
            "None", 75L, new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
            Arrays.asList(
                    new GiveBuffDTO(BuffTypeEnum.ATK, "", 3, 0.3F),
                    new GiveBuffDTO(BuffTypeEnum.MAGIC_ATK, "", 3, 0.3F)
            )), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void ifHit(BattleDTO from, BattleDTO tar,
                          List<BattleDTO> our, List<BattleDTO> enemy,
                          AtomicLong damage, boolean critical, StringBuilder builder) {
            from.getMarkMap().put("精铁寒利-斫霜", 1);
        }
    }),

    SKILL_1022(1022L, "昙月见景",
            "造成900%物理+300%法术伤害。召唤昙月持续三回合，昙月回合内星神免疫异常状态，昙月每回合追加攻击，造成星神80%法术伤害，" +
                    "追加攻击造成伤害后触发斫霜效果",
            "None", 150L, Arrays.asList(
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 9F),
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 3F)
    ), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void afterEffect(BattleDTO from, BattleDTO
                tar, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {
            BuffUtil.clearAbnormalBuff(from, builder);
            from.getMarkMap().put("昙月", 3);
            BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.IMMUNITY, "", 0L), 3, builder);
        }
    }, new ExtraBattleProcessTemplate() {
        @Override
        public void afterMyBehavior(BattleDTO tar, StringBuilder builder) {
            Map<String, Integer> markMap = this.getFrom().getMarkMap();
            if (markMap.containsKey("昙月")) {
                ExtraBattleProcessTemplate process = CopyUtil.copyNewInstance(SKILL_1018.globalExtraProcess);
                process.setFrom(this.getFrom());
                process.setOur(this.getOur());
                process.setEnemy(this.getEnemy());
                BattleUtil.executeSingleTarBehavior(SKILL_1023.getSkillEffects().get(0), SKILL_1023,
                        this.getFrom(), tar, this.getOur(), this.getEnemy(),
                        Collections.singletonList(process), builder);
                int cnt = markMap.get("昙月") - 1;
                if (cnt > 0) {
                    markMap.put("昙月", cnt);
                } else {
                    markMap.remove("昙月");
                }
            }
        }
    }),

    SKILL_1023(1023L, "昙月-追加伤害",
            "昙月每回合追加攻击，造成星神80%法术伤害",
            "None", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 0.8F)
    ),

    SKILL_1024(1024L, "摩诃无量",
            "战斗开始时提高自身60%攻击力持续六回合",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(List<String> battleMsg) {
            StringBuilder builder = new StringBuilder("※")
                    .append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动技能【摩诃无量】被触发");
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "摩诃无量", 0.6F), 6, builder);
            battleMsg.add(builder.toString());
        }
    }),

    SKILL_1025(1025L, "啜疾",
            "造成130%物理伤害，若未命中，则在使摩诃无量攻击提升回合数+1（为0时仍+1）",
            "None", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.3F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifNotHit(BattleDTO from, BattleDTO
                        tar, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {
                    BuffUtil.addBuff(from,
                            new BuffDTO(BuffTypeEnum.ATK, "摩诃无量", 0.6F, BuffOverrideStrategyEnum.ROUND_PLUS),
                            1, builder);
                }
            }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_1026(1026L, "世法解离",
            "脱离世法，不入轮回。造成450%物理伤害，行动开始时提升自身50%闪避持续三回合，回合结束时，使摩诃无量攻击提升回合数+3",
            "None", 80L, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 4.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void beforeEffect(BattleDTO from, BattleDTO
                        tar, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {
                    BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.DODGE, "", 0.5F), 3, builder);
                }

                @Override
                public void afterThisRound(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder
                        builder) {
                    BuffUtil.addBuff(from,
                            new BuffDTO(BuffTypeEnum.ATK, "摩诃无量", 0.6F, BuffOverrideStrategyEnum.ROUND_PLUS),
                            3, builder);
                }
            }
    ),

    SKILL_1027(1027L, "阔海无岸",
            "每次行动后提升自身5%速度，攻击命中后降低敌方30%血量回复效果持续两回合",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void processIfHitEnemy(BattleDTO tar, SkillEnum skillEnum, SkillEffectDTO skillEffect, AtomicLong damage,
                                      boolean critical, StringBuilder builder) {
            BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.HEAL, "", -0.3F), 2, builder);
        }

        @Override
        public void afterMyBehavior(BattleDTO tar, StringBuilder builder) {
            BuffUtil.addBuff(getFrom(), new BuffDTO(BuffTypeEnum.SPEED, "", 0.05F), 1000, builder);
        }
    }),

    SKILL_1028(1028L, "盈覆",
            "造成120%物理伤害，若此时自身速度达到1200点，则降低为80%物理伤害",
            "None", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.2F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public BigDecimal changeDamageRate(BattleDTO from, BattleDTO
                        tar, List<BattleDTO> our, List<BattleDTO> enemy, BigDecimal damageRate, StringBuilder builder) {
                    if (from.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed() >= 1200) {
                        return BigDecimal.valueOf(0.8F);
                    }
                    return damageRate;
                }
            }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_1029(1029L, "海合净明",
            "造成400%法术伤害，若此时速度达到1200点则额外使敌方眩晕一回合",
            "None", 40L, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 4F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleDTO from, BattleDTO tar, List<BattleDTO> our, List<BattleDTO> enemy, AtomicLong
                        damage, boolean critical, StringBuilder builder) {
                    if (from.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed() >= 1200) {
                        BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.PAUSE, "眩晕", 0L, true), 1, builder);
                    }
                }
            }
    ),

    SKILL_1030(1030L, "涤世",
            "清除敌方增益buff效果，若此时速度达到1200点，则提升自身25%攻击25%法强和25%闪避，持续三回合",
            "None", 60L, new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleDTO from, BattleDTO
                        tar, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {
                    enemy.forEach(e -> BuffUtil.clearActiveBuff(e, builder));
                }
            }
    ),

    SKILL_1031(1031L, "天水鸣叹",
            "造成1300%法术伤害，或若此时速度超过1200点，则开启天水，降低敌方全体25%闪避，25%法抗后，造成1700%法术伤害。" +
                    "天水持续时间内己方全体造成眩晕回合数额外+1 （已被眩晕同样+1回合）天水开启持续三回合",
            "None", 180L, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 13F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleDTO from, BattleDTO
                        tar, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {
                    if (from.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed() >= 1200) {
                        BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.DODGE, "", -0.25F), 3, builder);
                        BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.MAGIC_DEF, "", -0.25F), 3, builder);
                        our.forEach(o -> o.getMarkMap().put("天水", 3));
                    }
                }
            }, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeOurRound(List<String> battleMsg) {
            if (getFrom().getMarkMap().containsKey("天水")) {
                int round = getFrom().getMarkMap().get("天水") - 1;
                if (round < 0) {
                    getFrom().getMarkMap().remove("天水");
                } else {
                    getFrom().getMarkMap().put("天水", round);
                }
            }
        }
    }),

    SKILL_1032(1032L, "觉·摩诃无量",
            "战斗开始时提升自身100%攻击力，50%命中，50%穿甲，50%防御，50%法抗持续整场战斗，若该buff效果被清除，则五回合后重新激活" +
                    "所有摩诃无量同名效果只触发一条，且（觉>神>普>残）",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(List<String> battleMsg) {
            StringBuilder builder = new StringBuilder("※");
            builder.append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName()).append("的被动技能【觉·摩诃无量】被触发");
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "摩诃无量", 1F), 1000, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.HIT, "摩诃无量", 0.5F), 1000, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.PENETRATE, "摩诃无量", 0.5F), 1000, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.DEF, "摩诃无量", 0.5F), 1000, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.MAGIC_DEF, "摩诃无量", 0.5F), 1000, builder);
            battleMsg.add(builder.toString());
        }

        @Override
        public void beforeMyRound(List<String> battleMsg) {
            Map<String, Integer> markMap = this.getFrom().getMarkMap();
            if (markMap.containsKey("摩诃无量")) {
                if (markMap.get("摩诃无量") <= 1) {
                    markMap.remove("摩诃无量");
                    StringBuilder builder = new StringBuilder("※");
                    builder.append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName()).append("的被动技能【觉·摩诃无量】被触发");
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "摩诃无量", 1F), 1000, builder);
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.HIT, "摩诃无量", 0.5F), 1000, builder);
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.PENETRATE, "摩诃无量", 0.5F), 1000, builder);
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.DEF, "摩诃无量", 0.5F), 1000, builder);
                    BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.MAGIC_DEF, "摩诃无量", 0.5F), 1000, builder);
                    battleMsg.add(builder.toString());
                } else {
                    markMap.put("摩诃无量", markMap.get("摩诃无量") - 1);
                }
            } else {
                List<BattleBuffDTO> buffList = this.getFrom().getBuffMap().get(BuffTypeEnum.ATK);
                if (buffList.stream().noneMatch(buff -> "摩诃无量".equals(buff.getBuffDTO().getBuffName()))) {
                    markMap.put("摩诃无量", 5);
                }
            }
        }
    }),

    SKILL_1033(1033L, "魔慑·汲魂",
            "造成120%物理伤害同时从中汲取生命力，使此攻击附带20%物理吸血，每次攻击造成伤害时使敌方防御降低2%持续一回合，" +
                    "每使用一次该防御降低效果增加2%，最多不超过50%",
            "None", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.2F),
            new

                    ThisBehaviorExtraBattleProcessTemplate() {
                        @Override
                        public void ifHit(BattleDTO from, BattleDTO tar,
                                          List<BattleDTO> our, List<BattleDTO> enemy,
                                          AtomicLong damage, boolean critical, StringBuilder builder) {
                            BattleUtil.doHealing(from, Math.round(0.2F * damage.get()), builder);
                            int cnt = from.getMarkMap().getOrDefault("魔慑·汲魂", 0) + 1;
                            cnt = Math.min(cnt, 25);
                            from.getMarkMap().put("魔慑·汲魂", cnt);
                            BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.DEF, "", -0.02F * cnt), 1, builder);
                            BattleUtil.doHealing(from, Math.round(damage.get() * 0.2F), builder);
                        }
                    }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_1034(1034L, "伪.泯灭终释",
            "清除自身所有增益与弱化效果后对敌方造成800%攻击力的真实伤害",
            "None", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0F),
            new

                    ThisBehaviorExtraBattleProcessTemplate() {
                        @Override
                        public void ifHit(BattleDTO from, BattleDTO tar,
                                          List<BattleDTO> our, List<BattleDTO> enemy,
                                          AtomicLong damage, boolean critical, StringBuilder builder) {
                            BuffUtil.clearActiveBuff(from, builder);
                            BuffUtil.clearAbnormalBuff(from, builder);
                            BattleUtil.doRealDamage(tar, 8 * from.getOrganismInfoDTO().getOrganismDTO().getAtk(), builder);
                        }
                    }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_1035(1035L, "“勇者”（初）",
            "星神认可之“勇者”，战斗开始时，双攻，双防，闪避，命中，速度全部提升10%持续十回合，并附加对敌方1号位敌人造成15%法抗下降的法则效果",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(List<String> battleMsg) {
            StringBuilder builder = new StringBuilder("※");
            builder.append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName()).append("的被动技能【“勇者”（初）】被触发");
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "“勇者”（初）", 0.1F), 10, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.MAGIC_ATK, "“勇者”（初）", 0.1F), 10, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.DEF, "“勇者”（初）", 0.1F), 10, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.MAGIC_DEF, "“勇者”（初）", 0.1F), 10, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.HIT, "“勇者”（初）", 0.1F), 10, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.DODGE, "“勇者”（初）", 0.1F), 10, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.SPEED, "“勇者”（初）", 0.1F), 10, builder);
            RuleUtil.addRule(this.getEnemy().get(0), OrganismPropertiesEnum.MAGIC_DEF, "“勇者”（初）", -0.15F, builder);
            battleMsg.add(builder.toString());
        }
    }),

    SKILL_1036(1036L, "勇者之剑",
            "造成100%物理+40%法术伤害",
            "None", Arrays.asList(
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F),
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 0.4F)
    )),

    SKILL_1037(1037L, "蒙星天赐",
            "提升自身20%法强持续三回合，对敌方造成100%物理+350%法术伤害",
            "None", 100L, Arrays.asList(
            new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.MAGIC, 0F,
                    new GiveBuffDTO(BuffTypeEnum.MAGIC_ATK, "", 3, 0.2F)
            ),
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F),
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 3.5F)
    )),

    SKILL_1038(1038L, "年兽之力",
            "年兽的特殊能力，战斗开始时，当对方无人装备“轰天雷”时，属性大幅提升",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(List<String> battleMsg) {
            OrganismDTO organism = this.getFrom().getOrganismInfoDTO().getOrganismDTO();
            StringBuilder builder = new StringBuilder("※");
            if (this.getEnemy().stream()
                    .map(BattleDTO::getOrganismInfoDTO)
                    .map(OrganismInfoDTO::getEquipmentBarDTO)
                    .noneMatch(bar -> Objects.nonNull(bar)
                            && EquipmentEnum.EQUIPMENT_8.getId().equals(bar.getWeapon().getEquipmentId()))
            ) {
                builder.append(organism.getName()).append("的被动技能【年兽之力】被触发").append("，属性大幅提升");
                RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.ATK, "年兽之力", 8.88F, builder);
                RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.MAGIC_ATK, "年兽之力", 8.88F, builder);
                RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.DEF, "年兽之力", 8.88F, builder);
                RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.MAGIC_DEF, "年兽之力", 8.88F, builder);
                RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.HIT, "年兽之力", 8.88F, builder);
                RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.DODGE, "年兽之力", 8.88F, builder);
                RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.BEHAVIOR_SPEED, "年兽之力", 8.88F, builder);
                RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.CRITICAL_RATE, "年兽之力", 1.5F, builder);
                RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.CRITICAL_DAMAGE, "年兽之力", 10F, builder);
                RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.CRITICAL_REDUCTION_RATE, "年兽之力", 2F, builder);
                RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.CRITICAL_DAMAGE_REDUCTION, "年兽之力", 1F, builder);
                RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.LIFE_STEALING, "年兽之力", 8.88F, builder);
            } else {
                builder.append("因对方装备了“轰天雷”，").append(organism.getName()).append("的【年兽之力】失效");
            }
            battleMsg.add(builder.toString());
        }
    }),

    SKILL_1039(1039L, "审判（惩）", "提高自身50%攻击，50%法强",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(List<String> battleMsg) {
            StringBuilder builder = new StringBuilder("※")
                    .append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动技能【审判（惩）】被触发");
            RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.ATK, "审判（惩）", 0.5F, builder);
            RuleUtil.addRule(this.getFrom(), OrganismPropertiesEnum.MAGIC_ATK, "审判（惩）", 0.5F, builder);
            battleMsg.add(builder.toString());
        }
    }),

    SKILL_1040(1040L, "绝对公正", "自身回合开始时，清除敌我双方所有的增益和弱化类buff效果",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMyRound(List<String> battleMsg) {
            StringBuilder builder = new StringBuilder("※")
                    .append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动技能【绝对公正】被触发");
            this.getOur().forEach(o -> {
                BuffUtil.clearActiveBuff(o, builder);
                BuffUtil.clearInactiveBuff(o, builder);
            });
            this.getEnemy().forEach(o -> {
                BuffUtil.clearActiveBuff(o, builder);
                BuffUtil.clearInactiveBuff(o, builder);
            });
            battleMsg.add(builder.toString());
        }
    }),

    SKILL_1041(1041L, "镰斩", "造成150%物理伤害，命中后降低自身10%防御与法抗持续两回合",
            "None", new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleDTO from, BattleDTO tar,
                                  List<BattleDTO> our, List<BattleDTO> enemy,
                                  AtomicLong damage, boolean critical, StringBuilder builder) {
                    BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.DEF, "", -0.1F), 2, builder);
                    BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.MAGIC_DEF, "", -0.1F), 2, builder);
                }
            }
    ),

    SKILL_1042(1042L, "真·勇者", "星神认可，为世界带来希望之“勇者”，战斗开始时，双攻，双防，闪避，命中，速度全部提升20%持续十回合。" +
            "对敌方1号位敌人造成10%法抗10%物抗下降的法则效果",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(List<String> battleMsg) {
            StringBuilder builder = new StringBuilder("※");
            builder.append(this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName()).append("的被动技能【真·勇者】被触发");
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "真·勇者", 0.2F), 10, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.MAGIC_ATK, "真·勇者", 0.2F), 10, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.DEF, "真·勇者", 0.2F), 10, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.MAGIC_DEF, "真·勇者", 0.2F), 10, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.HIT, "真·勇者", 0.2F), 10, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.DODGE, "真·勇者", 0.2F), 10, builder);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.SPEED, "真·勇者", 0.2F), 10, builder);
            RuleUtil.addRule(this.getEnemy().get(0), OrganismPropertiesEnum.DEF, "真·勇者", -0.1F, builder);
            RuleUtil.addRule(this.getEnemy().get(0), OrganismPropertiesEnum.MAGIC_DEF, "真·勇者", -0.1F, builder);
            battleMsg.add(builder.toString());
        }
    }),

//    SKILL_1043(1043L, "明神", "解除自身所处的异常状态与弱化类buff效果，并提升自身40%闪避持续2回合",
//            "None", 40L
//
//    ),

    SKILL_1044(1044L, "离烟", "造成550%法术伤害，命中后扣除敌方10%蓝量",
            "None", 60L, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 5.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleDTO from, BattleDTO tar,
                                  List<BattleDTO> our, List<BattleDTO> enemy,
                                  AtomicLong damage, boolean critical, StringBuilder builder) {
                    BattleUtil.doMpRecover(tar, Math.round(-0.1F * tar.getOrganismInfoDTO().getOrganismDTO().getMaxMpWithAddition()), builder);
                }
            }
    ),

    SKILL_1045(1045L, "浮岸", "提升自身15%命中持续2回合，连续进行十次攻击每次攻击造成75%物理伤害",
            "None", 80L,
            Collections.nCopies(10, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.75F)),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void beforeThisRound(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {
                    BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.HIT, "", 0.15F), 2, builder);
                }
            }
    ),

    SKILL_1046(1046L, "祈愿", "敌我双方回复10%蓝量10%血量",
            "None", new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleDTO from, BattleDTO tar,
                                  List<BattleDTO> our, List<BattleDTO> enemy,
                                  AtomicLong damage, boolean critical, StringBuilder builder) {
                    our.forEach(o -> BattleUtil.doHealing(o,
                            Math.round(0.1F * o.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition()), builder));
                    our.forEach(o -> BattleUtil.doMpRecover(o,
                            Math.round(0.1F * o.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition()), builder));
                    enemy.forEach(e -> BattleUtil.doHealing(e,
                            Math.round(0.1F * e.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition()), builder));
                    enemy.forEach(e -> BattleUtil.doMpRecover(e,
                            Math.round(0.1F * e.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition()), builder));
                }
            }
    ),

//    SKILL_1047(1047L, "圣裁", "对敌方造成1200%法术伤害，命中后使敌方陷入焚毁异常持续两回合",
//            "None", 130L, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 12F),
//
//            ),

    SKILL_10000(10000L, "作弊",
            "开发者的作弊技能，造成10000%的物理伤害，并提升1000000攻击10回合",
            "All", 0L,
            Arrays.asList(
                    new SkillEffectDTO(SkillTargetEnum.ALL_ENEMY, DamageTypeEnum.ATK, 100F),
                    new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
                            new GiveBuffDTO(BuffTypeEnum.ATK, "作弊", 1000000L, 10, 1F)
                    )
            )
    ),

    ;
    /**
     * 技能编号
     */
    private final Long id;
    /**
     * 技能名称
     */
    private final String name;
    /**
     * 技能描述
     */
    private final String desc;
    /**
     * 限定职业编码
     * All代表所有职业均可使用
     * None代表所有职业均不可使用
     */
    private final List<String> jobCode;
    /**
     * 是否主动技能
     */
    private final Boolean active;
    /**
     * 耗蓝
     */
    private final Long mp;
    /**
     * 百分比耗蓝
     */
    private final Float mpRate;
    /**
     * 冷却回合数
     */
    private final Integer cd;
    /**
     * 技能效果
     */
    private final List<SkillEffectDTO> skillEffects;
    /**
     * 当前技能效果
     */
    private final ThisBehaviorExtraBattleProcessTemplate thisBehaviorExtraProcess;
    /**
     * 全局效果
     */
    private final ExtraBattleProcessTemplate globalExtraProcess;

    /**
     * 技能
     */
    private static final Map<Long, SkillEnum> SKILL_ID_MAP;
    /**
     * 技能
     */
    private static final Map<String, SkillEnum> SKILL_NAME_MAP;
    /**
     * 默认技能
     */
    private static final Map<String, Long> DEFAULT_SKILL_MAP = new HashMap<>();

    static {
        SKILL_ID_MAP = Arrays.stream(SkillEnum.values()).collect(Collectors.toMap(SkillEnum::getId, Function.identity()));
        SKILL_NAME_MAP = Arrays.stream(SkillEnum.values()).collect(Collectors.toMap(SkillEnum::getName, Function.identity()));

        DEFAULT_SKILL_MAP.put(JobEnum.XIU_ZHEN.getJobCode(), 1L);
        DEFAULT_SKILL_MAP.put(JobEnum.SI_DI_WU_SHI.getJobCode(), 2L);
        DEFAULT_SKILL_MAP.put(JobEnum.MAGICIAN.getJobCode(), 3L);
        DEFAULT_SKILL_MAP.put(JobEnum.GUN.getJobCode(), 4L);
        DEFAULT_SKILL_MAP.put(JobEnum.EVIL.getJobCode(), 5L);
        DEFAULT_SKILL_MAP.put(JobEnum.CHEATER.getJobCode(), 6L);
    }

    public static SkillEnum getById(Long id) {
        return SKILL_ID_MAP.get(id);
    }

    public static SkillEnum getByName(String name) {
        return SKILL_NAME_MAP.get(name);
    }

    public static SkillEnum getDefaultSkillByJob(String jobCode) {
        return getById(DEFAULT_SKILL_MAP.get(jobCode));
    }

    SkillEnum(Long id, String name, String desc, String jobCode, SkillEffectDTO skillEffect) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = 0L;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = Collections.singletonList(skillEffect);
        this.thisBehaviorExtraProcess = new ThisBehaviorExtraBattleProcessTemplate() {
        };
        this.globalExtraProcess = new ExtraBattleProcessTemplate() {
        };
    }

    SkillEnum(Long id, String name, String desc, String jobCode, SkillEffectDTO skillEffect,
              ThisBehaviorExtraBattleProcessTemplate thisBehaviorExtraProcess) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = 0L;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = Collections.singletonList(skillEffect);
        this.thisBehaviorExtraProcess = thisBehaviorExtraProcess;
        this.globalExtraProcess = new ExtraBattleProcessTemplate() {
        };
    }

    SkillEnum(Long id, String name, String desc, String jobCode, List<SkillEffectDTO> skillEffectList,
              ThisBehaviorExtraBattleProcessTemplate thisBehaviorExtraProcess) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = 0L;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = skillEffectList;
        this.thisBehaviorExtraProcess = thisBehaviorExtraProcess;
        this.globalExtraProcess = new ExtraBattleProcessTemplate() {
        };
    }

    SkillEnum(Long id, String name, String desc, String jobCode, SkillEffectDTO skillEffect,
              ThisBehaviorExtraBattleProcessTemplate thisBehaviorExtraProcess, ExtraBattleProcessTemplate globalExtraProcess) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = 0L;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = Collections.singletonList(skillEffect);
        this.thisBehaviorExtraProcess = thisBehaviorExtraProcess;
        this.globalExtraProcess = globalExtraProcess;
    }

    SkillEnum(Long id, String name, String desc, String jobCode, boolean active, ExtraBattleProcessTemplate process) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = active;
        this.mp = 0L;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = new ArrayList<>();
        this.thisBehaviorExtraProcess = new ThisBehaviorExtraBattleProcessTemplate() {
        };
        this.globalExtraProcess = process;
    }

    SkillEnum(Long id, String name, String desc, String jobCode, List<SkillEffectDTO> skillEffects) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = 0L;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = skillEffects;
        this.thisBehaviorExtraProcess = new ThisBehaviorExtraBattleProcessTemplate() {
        };
        this.globalExtraProcess = new ExtraBattleProcessTemplate() {
        };
    }

    SkillEnum(Long id, String name, String desc, String jobCode, Long mp, SkillEffectDTO skillEffect) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = mp;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = Collections.singletonList(skillEffect);
        this.thisBehaviorExtraProcess = new ThisBehaviorExtraBattleProcessTemplate() {
        };
        this.globalExtraProcess = new ExtraBattleProcessTemplate() {
        };
    }

    SkillEnum(Long id, String name, String desc, String jobCode, Long mp, List<SkillEffectDTO> skillEffects,
              ThisBehaviorExtraBattleProcessTemplate thisBehaviorExtraProcess) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = mp;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = skillEffects;
        this.thisBehaviorExtraProcess = thisBehaviorExtraProcess;
        this.globalExtraProcess = new ExtraBattleProcessTemplate() {
        };
    }

    SkillEnum(Long id, String name, String desc, String jobCode, Long mp, SkillEffectDTO skillEffect,
              ThisBehaviorExtraBattleProcessTemplate thisBehaviorExtraProcess) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = mp;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = Collections.singletonList(skillEffect);
        this.thisBehaviorExtraProcess = thisBehaviorExtraProcess;
        this.globalExtraProcess = new ExtraBattleProcessTemplate() {
        };
    }

    SkillEnum(Long id, String name, String desc, String jobCode, Long mp, SkillEffectDTO skillEffect,
              ThisBehaviorExtraBattleProcessTemplate thisBehaviorExtraProcess, ExtraBattleProcessTemplate globalExtraProcess) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = mp;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = Collections.singletonList(skillEffect);
        this.thisBehaviorExtraProcess = thisBehaviorExtraProcess;
        this.globalExtraProcess = globalExtraProcess;
    }

    SkillEnum(Long id, String name, String desc, String jobCode, Long mp, List<SkillEffectDTO> skillEffectList,
              ThisBehaviorExtraBattleProcessTemplate thisBehaviorExtraProcess, ExtraBattleProcessTemplate globalExtraProcess) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = mp;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = skillEffectList;
        this.thisBehaviorExtraProcess = thisBehaviorExtraProcess;
        this.globalExtraProcess = globalExtraProcess;
    }

    SkillEnum(Long id, String name, String desc, String jobCode, Long mp, List<SkillEffectDTO> skillEffects) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.jobCode = Collections.singletonList(jobCode);
        this.active = true;
        this.mp = mp;
        this.mpRate = 0F;
        this.cd = 0;
        this.skillEffects = skillEffects;
        this.thisBehaviorExtraProcess = new ThisBehaviorExtraBattleProcessTemplate() {
        };
        this.globalExtraProcess = new ExtraBattleProcessTemplate() {
        };
    }
}
