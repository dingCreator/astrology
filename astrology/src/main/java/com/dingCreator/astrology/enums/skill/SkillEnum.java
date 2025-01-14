package com.dingCreator.astrology.enums.skill;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.dto.BattleBuffDTO;
import com.dingCreator.astrology.dto.BattleDTO;
import com.dingCreator.astrology.dto.BuffDTO;
import com.dingCreator.astrology.dto.GiveBuffDTO;
import com.dingCreator.astrology.dto.organism.OrganismDTO;
import com.dingCreator.astrology.dto.skill.SkillEffectDTO;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.template.ExtraBattleProcessTemplate;
import com.dingCreator.astrology.template.ThisBehaviorExtraBattleProcessTemplate;
import com.dingCreator.astrology.util.*;
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
                    new GiveBuffDTO(BuffTypeEnum.HIT, "失神", -0.5F, 1, 0.1F)
            )
    ),

    SKILL_4(4L, "速射",
            "枪炮师引动枪弹进行五次射击，每次射击造成50%物理伤害，同时该技能每次伤害命中均能触发枪炮师伪神之眼效果。",
            JobEnum.GUN.getJobCode(),
            Collections.nCopies(5, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F))
    ),

    SKILL_5(5L, "汲魂之隙",
            "造成100%物理伤害同时从中汲取生命力，使此攻击附带12%物理吸血，每次攻击造成伤害时使敌方防御降低1%持续一回合，" +
                    "每使用一次该防御降低效果增加1%，最多不超过50%。",
            JobEnum.EVIL.getJobCode(), new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F,
            new GiveBuffDTO(BuffTypeEnum.DEF, "汲魂之隙", -0.01F, 5)
    ), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void ifHit(BattleDTO from, BattleDTO tar,
                          List<BattleDTO> our, List<BattleDTO> enemy,
                          AtomicLong damage, StringBuilder builder) {
            OrganismDTO organism = from.getOrganismInfoDTO().getOrganismDTO();
            long newHp = Math.min(organism.getMaxHpWithAddition(), organism.getHpWithAddition() + Math.round(damage.get() * 0.12F));
            builder.append("，血量恢复了").append(newHp - organism.getHpWithAddition());
            organism.setHpWithAddition(newHp);
        }
    }, new ExtraBattleProcessTemplate() {
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
            "星术师与敌人对战中领悟的伤害性技能，造成600%法术伤害。 蓝耗25",
            JobEnum.MAGICIAN.getJobCode(), 25L,
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
            "All", 0L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 3.5F)
    ),

    SKILL_13(13L, "鬼王游行",
            "化身鬼魅遨游战场，提高自身30%速度持续2回合，对敌方造成1000%物理伤害",
            "All", 0L,
            new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 10F, Collections.singletonList(
                    new GiveBuffDTO(BuffTypeEnum.SPEED, "", 2, 0.3F)
            ))
    ),

    SKILL_14(14L, "音噬",
            "攻击命中敌人时有30%概率使敌方进入回音状态，持续2回合。回音：造成伤害时受到造成伤害量15%的真实伤害（反伤）",
            JobEnum.MAGICIAN.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void processIfHit(BattleDTO tar, SkillEnum skillEnum, AtomicLong damage, StringBuilder builder) {
            if (RandomUtil.isHit(0.3F)) {
                BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.REFLECT_DAMAGE, "回音", 0.15F, true), 2, builder);
            }
        }
    }),

    SKILL_15(15L, "音敕",
            "造成110%法术伤害",
            "All", 0L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 1.1F)
    ),

    SKILL_16(16L, "诡惑迷音",
            "造成400%法术伤害并使敌方眩晕持续1回合",
            "All", 0L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 4F, Collections.singletonList(
                    new GiveBuffDTO(BuffTypeEnum.PAUSE, "眩晕", 1)
            ))
    ),

    SKILL_17(17L, "回音圣域",
            "对敌方全体造成600%法术伤害，并使敌方全体进入回音状态持续3回合。回音：造成伤害时受到造成伤害量15%的真实伤害（反伤）",
            "All", 0L,
            new SkillEffectDTO(SkillTargetEnum.ALL_ENEMY, DamageTypeEnum.MAGIC, 6F, Collections.singletonList(
                    new GiveBuffDTO(BuffTypeEnum.REFLECT_DAMAGE, "回音", 3)
            ))
    ),

    SKILL_18(18L, "普通攻击（法）",
            "造成100%法术伤害",
            "All", 0L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 1F)
    ),

    SKILL_19(19L, "汲咀之源",
            "造成200%法术伤害，技能命中时降低敌方10%法强，同时提高自身10%法强持续1回合",
            "All", 25L,
            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 2F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleDTO from, BattleDTO tar,
                                  List<BattleDTO> our, List<BattleDTO> enemy,
                                  AtomicLong damage, StringBuilder builder) {
                    BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.MAGIC_ATK, "", 0.1F), 1, builder);
                    BuffUtil.addBuff(tar, new BuffDTO(BuffTypeEnum.MAGIC_ATK, "", -0.1F), 1, builder);
                }
            }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_20(20L, "技能20",
            "",
            JobEnum.MAGICIAN.getJobCode(), 0L,
            new SkillEffectDTO()
    ),

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
        public void processIfHit(BattleDTO tar, SkillEnum skillEnum, AtomicLong damage, StringBuilder builder) {
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
            }, new ExtraBattleProcessTemplate() {
    }),

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
            }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_1011(1011L, "摩诃无量（残）",
            "战斗开始时，天无涯攻击力提升30%，持续三回合",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(List<String> battleMsg) {
            String msg = "※" + this.getFrom().getOrganismInfoDTO().getOrganismDTO().getName() + "的被动技能【摩诃无量（残）】被触发";
            StringBuilder builder = new StringBuilder(msg);
            BuffUtil.addBuff(this.getFrom(), new BuffDTO(BuffTypeEnum.ATK, "", 0.3F), 3, builder);
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
                public void ifHit(BattleDTO from, BattleDTO tar, List<BattleDTO> our, List<BattleDTO> enemy, AtomicLong damage, StringBuilder builder) {
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
                public void ifHit(BattleDTO from, BattleDTO tar, List<BattleDTO> our, List<BattleDTO> enemy, AtomicLong damage, StringBuilder builder) {
                    OrganismDTO fromOrganismDTO = from.getOrganismInfoDTO().getOrganismDTO();
                    long magicAtk = BattleUtil.getLongProperty(fromOrganismDTO.getMagicAtk(), BuffTypeEnum.MAGIC_ATK.getName(), from);
                    long oldHp = fromOrganismDTO.getHpWithAddition();
                    long newHp = oldHp + BigDecimalUtil.multiply(magicAtk, 1.3F);
                    newHp = newHp > fromOrganismDTO.getMaxHpWithAddition() ? fromOrganismDTO.getMaxHpWithAddition() : newHp;
                    builder.append("，").append(fromOrganismDTO.getName()).append("血量回复").append(newHp - oldHp).append("点");
                    fromOrganismDTO.setMpWithAddition(newHp);
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
        public void processIfHit(BattleDTO tar, SkillEnum skillEnum, AtomicLong damage, StringBuilder builder) {
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
        public void ifHit(BattleDTO from, BattleDTO tar, List<BattleDTO> our, List<BattleDTO> enemy, AtomicLong damage, StringBuilder builder) {
            if (damage.get() > 0) {
                List<BattleBuffDTO> buffList = tar.getBuffMap().get(BuffTypeEnum.SPEED);
                if (CollectionUtil.isNotEmpty(buffList)
                        && buffList.stream().anyMatch(buff -> "斫霜".equals(buff.getBuffDTO().getBuffName()))) {
                    tar.getMarkMap().put("莹雪化谢-斫霜", 1);
                }
            }
        }

        @Override
        public void afterThisRound(BattleDTO from, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {
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
        public void ifHit(BattleDTO from, BattleDTO tar, List<BattleDTO> our, List<BattleDTO> enemy, AtomicLong damage,
                          StringBuilder builder) {
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
        public void afterEffect(BattleDTO from, BattleDTO tar, List<BattleDTO> our, List<BattleDTO> enemy, StringBuilder builder) {
            BuffUtil.clearAbnormalBuff(from, builder);
            from.getMarkMap().put("昙月", 3);
            BuffUtil.addBuff(from, new BuffDTO(BuffTypeEnum.IMMUNITY, "", 0L), 3, builder);
        }
    }, new ExtraBattleProcessTemplate() {
        @Override
        public void afterMyBehavior(BattleDTO tar, StringBuilder builder) {
            Map<String, Integer> markMap = this.getFrom().getMarkMap();
            if (markMap.containsKey("昙月")) {
                BattleUtil.executeBehavior(SKILL_1023.getSkillEffects().get(0), SKILL_1023,
                        this.getFrom(), tar, this.getOur(), this.getEnemy(),
                        Collections.singletonList(SKILL_1018.globalExtraProcess), builder);
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

    SKILL_1024(1024L, "技能1024",
            "",
            "None", new SkillEffectDTO()
    ),

    SKILL_1025(1025L, "技能1025",
            "",
            "None", new SkillEffectDTO()
    ),

    SKILL_1026(1026L, "技能1026",
            "",
            "None", new SkillEffectDTO()
    ),

    SKILL_1027(1027L, "技能1027",
            "",
            "None", new SkillEffectDTO()
    ),

    SKILL_1028(1028L, "技能1028",
            "",
            "None", new SkillEffectDTO()
    ),

    SKILL_1029(1029L, "技能1029",
            "",
            "None", new SkillEffectDTO()
    ),

    SKILL_1030(1030L, "技能1030",
            "",
            "None", new SkillEffectDTO()
    ),

    SKILL_1031(1031L, "技能1031",
            "",
            "None", new SkillEffectDTO()
    ),

    SKILL_1032(1032L, "技能1032",
            "",
            "None", new SkillEffectDTO()
    ),

    SKILL_1033(1033L, "技能1033",
            "",
            "None", new SkillEffectDTO()
    ),

    SKILL_1034(1034L, "技能1034",
            "",
            "None", new SkillEffectDTO()
    ),

    SKILL_1035(1035L, "技能1035",
            "",
            "None", new SkillEffectDTO()
    ),

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
