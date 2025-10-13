package com.dingCreator.astrology.enums.skill;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.battle.*;
import com.dingCreator.astrology.dto.organism.OrganismDTO;
import com.dingCreator.astrology.dto.organism.OrganismInfoDTO;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.dto.skill.SkillEffectDTO;
import com.dingCreator.astrology.enums.BuffOverrideStrategyEnum;
import com.dingCreator.astrology.enums.EffectTypeEnum;
import com.dingCreator.astrology.enums.FieldEffectEnum;
import com.dingCreator.astrology.enums.OrganismPropertiesEnum;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.util.*;
import com.dingCreator.astrology.util.template.ExtraBattleProcessTemplate;
import com.dingCreator.astrology.util.template.ThisBehaviorExtraBattleProcessTemplate;
import com.dingCreator.astrology.util.template.ThisEffectExtraBattleProcessTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ding
 * @date 2024/2/4
 */
@Getter
@AllArgsConstructor
@Accessors(chain = true)
public enum SkillEnum implements Serializable {
    /**
     * 技能列表
     */
    SKILL_1(1L, "御剑术", "修真者使用仙剑进行攻击，对敌方造成90%物理伤害", JobEnum.XIU_ZHEN.getJobCode(),
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.9F)),

    SKILL_2(2L, "顺劈", "死地武士挥动武士刀进行劈砍，对敌方造成120%物理伤害", JobEnum.SI_DI_WU_SHI.getJobCode(),
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.2F)),

    SKILL_3(3L, "唤星术", "星星具有蛊惑人的力量，星术师发动唤星术造成100%法术伤害的同时，有10%概率使对方附加“失神”异常持续1回合",
            JobEnum.MAGICIAN.getJobCode(), new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 1F,
            new ThisEffectExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    if (RandomUtil.isHit(0.1F)) {
                        AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                                .from(battleEffect.getFrom()).tar(battleEffect.getTar())
                                .builder(battleEffect.getBattleRound().getBuilder())
                                .round(1).build();
                        AbnormalEnum.ABSENT_MINDED.doEffect(input);
                    }
                }
            }
    )),

    SKILL_4(4L, "速射/垂怜", "枪炮师引动枪弹进行四次射击，每次射击造成50%物理伤害。伪神姿态下，改为100%", JobEnum.GUN.getJobCode(),
            Collections.nCopies(4, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F)),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void changeDamageRate(BattleEffectDTO battleEffect) {
                    if (battleEffect.getFrom().getMarkMap().containsKey("伪神")) {
                        battleEffect.setDamageRate(BigDecimal.ONE);
                    }
                }
            }
    ) {
        @Override
        public String getName(BattleRoundDTO battleRound) {
            if (battleRound.getFrom().getMarkMap().containsKey("伪神")) {
                return "垂怜";
            }
            return "速射";
        }
    },

    SKILL_5(5L, "汲魂之隙",
            "造成100%物理伤害同时从中汲取生命力，使此攻击附带12%物理吸血，每次攻击造成伤害时使敌方防御降低1%持续一回合，" +
                    "每使用一次该防御降低效果增加1%，最多不超过50%。",
            JobEnum.EVIL.getJobCode(), new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F
    ), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void ifHit(BattleEffectDTO battleEffect) {
            BattleDTO from = battleEffect.getFrom();
            BattleDTO tar = battleEffect.getTar();
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();

            int cnt = from.getMarkMap().getOrDefault("汲魂之隙", 0) + 1;
            cnt = Math.min(cnt, 50);
            from.getMarkMap().put("汲魂之隙", cnt);
            BuffUtil.addBuff(from, tar, new BuffDTO(EffectTypeEnum.DEF, "", -0.01F * cnt), 1, builder);
            BattleUtil.doHealing(from, Math.round(battleEffect.getDamage().get() * 0.12F), builder,
                    battleEffect.getBattleRound().getBattleField());
        }
    }),

    SKILL_6(6L, "芬芳",
            "小嘴抹蜜口吐芬芳，对敌方全体造成80%物理伤害并且有10%概率嘲讽对方1回合",
            Constants.ALL,
            new SkillEffectDTO(TargetEnum.ALL_ENEMY, DamageTypeEnum.ATK, 0.8F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    if (RandomUtil.isHit(0.1F)) {
                        BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                                new BuffDTO(EffectTypeEnum.TAUNT, "芬芳", true),
                                1, battleEffect.getBattleRound().getBuilder());
                    }
                }
            }
    ),

    SKILL_7(7L, "坠星珠", "星术师与敌人对战中领悟的伤害性技能，造成600%法术伤害", JobEnum.MAGICIAN.getJobCode(),
            35L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 6F)
    ),

    SKILL_8(8L, "无罔归一", "修真者领悟御剑归一之境，能够对敌人造成450%物理伤害", JobEnum.XIU_ZHEN.getJobCode(),
            15L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 4.5F)
    ),

    SKILL_9(9L, "星之宠儿",
            "星术师作为星术界天才中的天才，他天生拥有对星术的超强控制力，每场战斗开始时能够免疫10%法术伤害，同时提高自身15%法强，持续整场战斗",
            JobEnum.MAGICIAN.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(BattleFieldDTO battleField) {
            if (Objects.nonNull(this.getOwner())) {
                StringBuilder builder = new StringBuilder("※" + getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                        .append("的被动【").append(SKILL_9.getName()).append("】被触发");
                BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                        new BuffDTO(EffectTypeEnum.MAGIC_ATK, "", 0.15F), 1000, builder);
                BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                        new BuffDTO(EffectTypeEnum.MAGIC_DAMAGE, "", -0.1F), 1000, builder);
                battleField.getBattleMsg().add(builder.toString());
            }
        }
    }),

    SKILL_10(10L, "霸刀罡气", "霸刀门独门绝技，能透过防御直达实体，增加自身25%穿透与25%法穿", Constants.ALL,
            false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(BattleFieldDTO battleField) {
            StringBuilder builder = new StringBuilder("※" + getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动【").append(SKILL_10.getName()).append("】被触发");
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.PENETRATE, "", 0.25F), 1000, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.MAGIC_DAMAGE, "", 0.25F), 1000, builder);
            battleField.getBattleMsg().add(builder.toString());
        }
    }),

    SKILL_11(11L, "普通攻击（物）", "造成110%物理伤害",
            Constants.ALL, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.1F)
    ),

    SKILL_12(12L, "地裂斩", "造成350%物理伤害", Constants.ALL, 30L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 3.5F)
    ),

    SKILL_13(13L, "鬼王游行", "化身鬼魅遨游战场，提高自身30%速度持续2回合，对敌方造成1000%物理伤害",
            Constants.ALL, 60L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 10F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.SPEED, "", 0.3F),
                            2, battleEffect.getBattleRound().getBuilder());
                }
            }
    ),

    SKILL_14(14L, "音噬", "攻击命中敌人时有30%概率使敌方进入回音状态，持续2回合",
            JobEnum.MAGICIAN.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void ifMeHitEnemy(BattleEffectDTO battleEffect) {
            if (RandomUtil.isHit(0.3F)) {
                AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                        .from(battleEffect.getFrom()).tar(battleEffect.getTar()).round(2)
                        .builder(battleEffect.getBattleRound().getBuilder()).build();
                AbnormalEnum.ECHO.doEffect(input);
            }
        }
    }),

    SKILL_15(15L, "音敕", "造成110%法术伤害", Constants.ALL,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 1.1F)
    ),

    SKILL_16(16L, "诡惑迷音", "造成400%法术伤害并使敌方眩晕持续1回合", Constants.ALL, 30L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 4F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                            .from(battleEffect.getFrom()).tar(battleEffect.getTar()).round(1)
                            .builder(battleEffect.getBattleRound().getBuilder()).build();
                    AbnormalEnum.VERTIGO.doEffect(input);
                }
            }
    ),

    SKILL_17(17L, "回音圣域", "对敌方全体造成600%法术伤害，并使敌方全体进入回音状态持续3回合", Constants.ALL,
            60L, new SkillEffectDTO(TargetEnum.ALL_ENEMY, DamageTypeEnum.MAGIC, 6F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                            .from(battleEffect.getFrom()).tar(battleEffect.getTar()).round(3)
                            .builder(battleEffect.getBattleRound().getBuilder()).build();
                    AbnormalEnum.ECHO.doEffect(input);
                }
            }
    ),

    SKILL_18(18L, "普通攻击（法）", "造成100%法术伤害", Constants.ALL,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 1F)
    ),

    SKILL_19(19L, "汲咀之源", "造成200%法术伤害，技能命中时降低敌方10%法强，同时提高自身10%法强持续1回合", Constants.ALL,
            25L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 2F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    BattleDTO tar = battleEffect.getTar();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();

                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.MAGIC_ATK, "", 0.1F), 1, builder);
                    BuffUtil.addBuff(from, tar, new BuffDTO(EffectTypeEnum.MAGIC_ATK, "", -0.1F), 1, builder);
                }
            }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_20(20L, "源星盾",
            "星术师操控星辰力形成护罩缠绕自身，能够抵挡敌方一次伤害，同时提高自身20%法强和20%命中持续3回合",
            JobEnum.MAGICIAN.getJobCode(), 60L,
            new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.MAGIC, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();

                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.MAGIC_ATK, 0.2F), 3, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.HIT, 0.2F), 3, builder);
                    RuleUtil.addRule(from, from, EffectTypeEnum.TIMES_SHIELD, "源星盾", 1, builder);
                }
            }
    ),

    SKILL_21(21L, "侵蚀雾霭",
            "死地武士周身缠绕着经久不散的雾霭，他的攻击会附加（10%攻击力*技能倍率）的真实伤害。并给被死地武士击中的单位附加雾霭异常持续1回合",
            JobEnum.SI_DI_WU_SHI.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void ifMeHitEnemy(BattleEffectDTO battleEffect) {
            OrganismDTO fromOrganism = this.getOwner().getOrganismInfoDTO().getOrganismDTO();
            BattleUtil.doRealDamage(
                    battleEffect.getTar(),
                    Math.round(0.1 * fromOrganism.getAtk() * battleEffect.getDamageRate().floatValue()),
                    battleEffect.getBattleRound().getBuilder()
            );
            AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                    .from(battleEffect.getFrom()).tar(battleEffect.getTar()).round(1)
                    .builder(battleEffect.getBattleRound().getBuilder()).build();
            AbnormalEnum.MIST.doEffect(input);
        }
    }),

    SKILL_22(22L, "伪神之眼",
            "枪炮师拥有未元之都的尖端科技产物——伪神之眼，能够将视域扩大到方圆百公里，每次攻击命中时都会为枪炮师提供2%穿透和5%命中提升效果持续两回合，" +
                    "可叠加，最高不得超过20%和50%",
            JobEnum.GUN.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void ifMeHitEnemy(BattleEffectDTO battleEffect) {
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();

            Map<EffectTypeEnum, List<BattleBuffDTO>> buffMap = this.getOwner().getBuffMap();
            BigDecimal penetrateRate = buffMap.getOrDefault(EffectTypeEnum.PENETRATE, new ArrayList<>()).stream()
                    .map(BattleBuffDTO::getBuffDTO)
                    .filter(buffDTO -> "伪神之眼".equals(buffDTO.getBuffName()))
                    .map(BuffDTO::getRate).reduce((rate1, rate2) -> rate2.add(rate1)).orElse(BigDecimal.ZERO);
            if (penetrateRate.compareTo(BigDecimal.valueOf(0.2)) <= 0) {
                if (!builder.toString().contains("伪神之眼")) {
                    builder.append("，").append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的被动【伪神之眼】被触发");
                }
                BuffDTO buffDTO = new BuffDTO(EffectTypeEnum.PENETRATE, "伪神之眼", 0.02F,
                        BuffOverrideStrategyEnum.NONINTERFERENCE);
                BuffUtil.addBuff(this.getOwner(), this.getOwner(), buffDTO, 2, builder);
            }

            BigDecimal hitRate = buffMap.getOrDefault(EffectTypeEnum.HIT, new ArrayList<>()).stream()
                    .map(BattleBuffDTO::getBuffDTO)
                    .filter(buffDTO -> "伪神之眼".equals(buffDTO.getBuffName()))
                    .map(BuffDTO::getRate).reduce((rate1, rate2) -> rate2.add(rate1)).orElse(BigDecimal.ZERO);
            if (hitRate.compareTo(BigDecimal.valueOf(0.5)) <= 0) {
                if (!builder.toString().contains("伪神之眼")) {
                    builder.append("，").append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的被动【伪神之眼】被触发");
                }
                BuffDTO buffDTO = new BuffDTO(EffectTypeEnum.HIT, "伪神之眼", 0.05F,
                        BuffOverrideStrategyEnum.NONINTERFERENCE);
                BuffUtil.addBuff(this.getOwner(), this.getOwner(), buffDTO, 2, builder);
            }
        }
    }),

    SKILL_23(23L, "怯勇",
            "同低于等于自身境界的对手（包括BOSS）战斗时额外获得20%减伤和24%攻击加成，若低于敌方等级则额外受到15%伤害",
            JobEnum.EVIL.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(BattleFieldDTO battleField) {
            StringBuilder builder = new StringBuilder("※" + getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动【怯勇】被触发");
            int fromRank = getOwner().getOrganismInfoDTO().getOrganismDTO().getRank();
            int highestTarRank = battleField.getEnemy(this.getOwner()).stream()
                    .map(BattleDTO::getOrganismInfoDTO)
                    .map(OrganismInfoDTO::getOrganismDTO)
                    .mapToInt(OrganismDTO::getRank).max().orElse(0);
            if (fromRank >= highestTarRank) {
                BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.DAMAGE, -0.2F), builder);
                BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.ATK, 0.24F), builder);
            } else {
                BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.DAMAGE, 0.15F), builder);
            }
            battleField.getBattleMsg().add(builder.toString());
        }
    }),

    SKILL_24(24L, "剑魂",
            "修真者拥有极高的剑道天赋，每次出剑都会更加凌厉。其每次出手前使自身攻击力提高5%",
            JobEnum.XIU_ZHEN.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMyBehavior(BattleEffectDTO battleEffect) {
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            builder.append(",").append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动【剑魂】被触发");
            BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.ATK, "", 0.05F),
                    1000, builder);
        }
    }),

    SKILL_25(25L, "凌锋踏步",
            "修真者引动自身气旋，提高自身50%速度和50%攻击力持续3回合。装备此技能后，每回合回复自身蓝量的5%",
            JobEnum.XIU_ZHEN.getJobCode(), 90L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO tar = battleEffect.getTar();
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.addBuff(from, tar, new BuffDTO(EffectTypeEnum.SPEED, 0.5F), 3, builder);
                    BuffUtil.addBuff(from, tar, new BuffDTO(EffectTypeEnum.ATK, 0.5F), 3, builder);
                }
            },
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeMyRound(BattleRoundDTO battleRound) {
                    battleRound.getBuilder().append("※")
                            .append(battleRound.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的技能【凌锋踏步】被触发");
                    BattleUtil.doMpChange(
                            battleRound.getFrom(),
                            Math.round(battleRound.getFrom().getOrganismInfoDTO().getOrganismDTO().getMaxMpWithAddition() * 0.05F),
                            battleRound.getBuilder()
                    );
                }
            }
    ),

    SKILL_26(26L, "战衅怒吼",
            "死地武士抓取一名敌人对其造成500%物理伤害并给自身附加嘲讽状态，持续2回合",
            JobEnum.SI_DI_WU_SHI.getJobCode(), 50L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.TAUNT, "", true),
                            2, battleEffect.getBattleRound().getBuilder());
                }
            }
    ),

    SKILL_27(27L, "羽破千翎",
            "修真者进行快速连续的四段斩击，每段造成150%物理伤害",
            JobEnum.XIU_ZHEN.getJobCode(), 30L,
            Collections.nCopies(4, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.5F))
    ),

    SKILL_28(28L, "煜塞天河",
            "修真者感悟天地之力，能够使用天地之力对敌方进行封锁，使进入眩晕异常2回合",
            JobEnum.XIU_ZHEN.getJobCode(), 80L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                            .from(battleEffect.getFrom()).tar(battleEffect.getTar()).round(2)
                            .builder(battleEffect.getBattleRound().getBuilder()).build();
                    AbnormalEnum.VERTIGO.doEffect(input);
                }
            }
    ),

    SKILL_29(29L, "万里无垠", "修真者突破极巅领悟的招式，对敌人造成1200%物理伤害。" +
            "展开无垠领域，己方单位全体获得25%攻击与25%速度加成，敌方全体单位降低10%防御与10%速度。无垠领域展开持续5回合",
            JobEnum.XIU_ZHEN.getJobCode(), 120L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 12F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    battleEffect.getOur().forEach(b -> {
                        BuffUtil.addBuff(battleEffect.getFrom(), b,
                                new BuffDTO(EffectTypeEnum.ATK, "无垠领域", 0.25F), 5, builder);
                        BuffUtil.addBuff(battleEffect.getFrom(), b,
                                new BuffDTO(EffectTypeEnum.SPEED, "无垠领域", 0.25F), 5, builder);
                    });
                }

                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    battleEffect.getEnemy().forEach(b -> {
                        BuffUtil.addBuff(battleEffect.getFrom(), b,
                                new BuffDTO(EffectTypeEnum.DEF, "无垠领域", -0.1F), 5, builder);
                        BuffUtil.addBuff(battleEffect.getFrom(), b,
                                new BuffDTO(EffectTypeEnum.SPEED, "无垠领域", -0.1F), 5, builder);
                    });
                }
            }
    ),

    SKILL_30(30L, "狂暴烈斩",
            "死地武士进行猛烈斩击，造成600%物理伤害，并降低自身10%速度",
            JobEnum.SI_DI_WU_SHI.getJobCode(), 15L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 6F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.SPEED, -0.1F),
                            1, battleEffect.getBattleRound().getBuilder()
                    );
                }
            }
    ),

    SKILL_31(31L, "蒙络坠影",
            "死地武士使驱使雾霭缠绕自身，增加自身20%防御力和30%法抗并可以反弹受到伤害的15%的真实伤害，持续3回合",
            JobEnum.SI_DI_WU_SHI.getJobCode(), 80L,
            new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.DEF, "", 0.2F), 3, builder);
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.MAGIC_DEF, "", 0.3F), 3, builder);
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.REFLECT_DAMAGE, "蒙络坠影", 0.15F),
                            3, builder
                    );
                }
            }
    ),

    SKILL_32(32L, "浴血神战",
            "造成800%物理伤害。装备此技能后，在战斗濒临死亡之际会爆发绝境的意志，受到致命伤害不会死亡，并回复自身全部血量和蓝量，" +
                    "但会降低自身60%攻击力持续整场战斗。每场战斗仅触发一次",
            JobEnum.SI_DI_WU_SHI.getJobCode(), 80L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 6F),
            new ThisBehaviorExtraBattleProcessTemplate() {
            },
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeMeDeath(BattleEffectDTO battleEffect) {
                    Map<String, Integer> markMap = battleEffect.getTar().getMarkMap();
                    if (markMap.containsKey("浴血神战")) {
                        return;
                    }
                    BattleDTO tar = battleEffect.getTar();
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    // 因为是受到致命伤害，此处的from应为造成伤害者，tar才是受到致命伤害者
                    battleEffect.getDamage().set(0L);
                    builder.append("，☠").append(tar.getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("受到致命伤害，技能【浴血神战】效果被触发，免疫此次伤害");
                    BattleUtil.doHealing(tar, tar.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition(), builder,
                            battleEffect.getBattleRound().getBattleField());
                    BattleUtil.doMpChange(tar, tar.getOrganismInfoDTO().getOrganismDTO().getMaxMpWithAddition(), builder);
                    RuleUtil.addRule(from, from, EffectTypeEnum.ATK, "浴血神战", -0.6F, builder);
                    markMap.put("浴血神战", 1);
                }
            }
    ),

    SKILL_33(33L, "修罗法身",
            "死地武士爆发全部雾霭的力量，开启狂暴效果，狂暴开启时清除自身减益和异常，同时5回合内不受异常和buff类弱化效果影响。" +
                    "5回合内提升自身30%血量上限和20%防御、法抗和25%攻击，同时将被动技能的真实伤害效果提高到50%持续5回合。" +
                    "5回合内每回合回复自身10%血量",
            // todo 限定回合的血量上限变化
            JobEnum.SI_DI_WU_SHI.getJobCode(), 120L,
            new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();

                    BuffUtil.clearAbnormalBuff(from, builder);
                    BuffUtil.clearInactiveBuff(from, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.ATK, "", 0.25F), 5, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.DEF, "", 0.2F), 5, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.MAGIC_DEF, "", 0.2F), 5, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.IMMUNITY, "修罗法身", 0L), 5, builder);
                }
            }
    ),

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
        public void beforeMyRound(BattleRoundDTO battleRound) {
            StringBuilder builder = new StringBuilder("※" + getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动【九龙归允——太初法则】被触发");
            int mode;
            if (this.getOwner().getMarkMap().containsKey("太初神誓")) {
                mode = (1 << 8) + (1 << 7) + (1 << 6) + (1 << 5) + (1 << 4) + (1 << 3) + (1 << 2) + (1 << 1) + 1;
            } else {
                mode = 1 << RandomUtil.rangeIntRandom(9);
            }
            for (int i = 0; i < 9; i++) {
                if ((mode >> i) % 2 == 1) {
                    switch (i) {
                        case 0:
                            builder.append("，触发【鸣翔之龙】");
                            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                                    new BuffDTO(EffectTypeEnum.SPEED, "", 0.3F), 1, builder);
                            break;
                        case 1:
                            builder.append("，触发【破云之龙】");
                            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                                    new BuffDTO(EffectTypeEnum.ATK, "", 0.3F), 1, builder);
                            break;
                        case 2:
                            builder.append("，触发【镇世之龙】");
                            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                                    new BuffDTO(EffectTypeEnum.DEF, "", 0.3F), 1, builder);
                            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                                    new BuffDTO(EffectTypeEnum.MAGIC_DEF, "", 0.3F), 1, builder);
                            break;
                        case 3:
                            builder.append("，触发【惊霆之龙】");
                            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                                    new BuffDTO(EffectTypeEnum.PENETRATE, "", 0.3F), 1, builder);
                            break;
                        case 4:
                            builder.append("，触发【山海之龙】");
                            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                                    new BuffDTO(EffectTypeEnum.HIT, "", 0.3F), 1, builder);
                            break;
                        case 5:
                            builder.append("，触发【圣洁之龙】");
                            BuffUtil.clearAbnormalBuff(this.getOwner(), builder);
                            break;
                        case 6:
                            builder.append("，触发【彼岸之龙】");
                            this.getOwner().getMarkMap().put("彼岸之龙", 1);
                            break;
                        case 7:
                            builder.append("，触发【湮灭之龙】");
                            long atk = BattleUtil.getLongProperty(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getAtk(),
                                    OrganismPropertiesEnum.ATK.getFieldName(), this.getOwner(), battleRound.getBattleField());
                            long realDamage = Math.round(atk * 0.5F);
                            battleRound.getEnemy().forEach(e -> BattleUtil.doRealDamage(e, realDamage, builder));
                            break;
                        case 8:
                            builder.append("，触发【自然之龙】");
                            long heal = Math.round(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition() * 0.1F);
                            BattleUtil.doHealing(this.getOwner(), heal, builder, battleRound.getBattleField());
                            break;
                        default:
                            builder.append("技能使用失败");
                            break;
                    }
                }
            }
            battleRound.getBattleField().getBattleMsg().add(builder.toString());
        }

        @Override
        public void beforeMeDeath(BattleEffectDTO battleEffect) {
            Map<String, Integer> markMap = this.getOwner().getMarkMap();
            if (markMap.containsKey("彼岸之龙") && !markMap.containsKey("已触发彼岸之龙")) {
                battleEffect.getDamage().set(0);
                markMap.put("已触发彼岸之龙", 1);
                markMap.remove("彼岸之龙");
                StringBuilder builder = battleEffect.getBattleRound().getBuilder();

                BattleDTO battleDTO = this.getOwner();
                OrganismDTO organismDTO = battleDTO.getOrganismInfoDTO().getOrganismDTO();
                builder.append("，☠").append(organismDTO.getName()).append("受到致命伤害，【彼岸之龙】效果被触发，复活");
                BattleUtil.doHealing(battleDTO, organismDTO.getMaxHpWithAddition(), builder,
                        battleEffect.getBattleRound().getBattleField());
                BattleUtil.doMpChange(battleDTO, organismDTO.getMaxMpWithAddition(), builder);
                BuffUtil.clearAllBuff(battleDTO, builder);
            }
        }
    }),

    SKILL_35(35L, "九龙僭越",
            "对敌方造成九次60%的物理伤害，每段伤害命中时降低敌方2%防御，持续一回合",
            "All", 40L, Collections.nCopies(9,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.6F)),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getTar(),
                            new BuffDTO(EffectTypeEnum.DEF, "", -0.02F),
                            1, battleEffect.getBattleRound().getBuilder());
                }
            }
    ),

    SKILL_36(36L, "太初神誓",
            "全力催动祖龙血脉，释放太初法则，三回合内，每回合九龙效果全部生效",
            "All", 100L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterBehavior(BattleRoundDTO battleRound) {
                    BattleDTO from = battleRound.getFrom();
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
                public void ifHit(BattleEffectDTO battleEffect) {
                    battleEffect.getFrom().getMarkMap().put("太初神誓", 3);
                }
            }
    ),

    SKILL_37(37L, "惘星叹",
            "星术师的星术变幻莫测能够扰乱敌方的神志，造成750%法术伤害，同时使敌方失神持续2回合，如果命中时敌方已经失神则额外造成眩晕效果持续1回合",
            JobEnum.MAGICIAN.getJobCode(), 100L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 7.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO tar = battleEffect.getTar();
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    List<BattleBuffDTO> buffList = tar.getBuffMap().get(EffectTypeEnum.HIT);
                    if (CollectionUtil.isNotEmpty(buffList)
                            && buffList.stream().anyMatch(buff -> "失神".equals(buff.getBuffDTO().getBuffName()))) {
                        AbnormalEnum.AbnormalInput input1 = AbnormalEnum.AbnormalInput.builder()
                                .from(from).tar(tar).round(1).builder(builder).build();
                        AbnormalEnum.VERTIGO.doEffect(input1);
                    }
                    AbnormalEnum.AbnormalInput input2 = AbnormalEnum.AbnormalInput.builder()
                            .from(from).tar(tar).round(2).builder(builder).build();
                    AbnormalEnum.ABSENT_MINDED.doEffect(input2);
                }
            }
    ),

    SKILL_38(38L, "幻星神",
            "星术师操纵星术的能力出神入化，回复己方全体20%蓝量并提高自身50%速度持续2回合",
            JobEnum.MAGICIAN.getJobCode(), 150L,
            new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.MAGIC, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    battleEffect.getOur().forEach(o -> {
                        long maxMp = o.getOrganismInfoDTO().getOrganismDTO().getMaxMpWithAddition();
                        BattleUtil.doMpChange(o, Math.round(0.2 * maxMp), builder);
                    });
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.SPEED, "", 0.5F), 2, builder);
                }
            }
    ),

    SKILL_39(39L, "星坠之时·来自宇宙",
            "出手前提高自身命中999999999和15%法穿持续0回合，星术师施展的星术终极奥义，对敌方全体造成1800%法术伤害同时对敌方全体附加失神1回合",
            JobEnum.MAGICIAN.getJobCode(), 150L,
            new SkillEffectDTO(TargetEnum.ALL_ENEMY, DamageTypeEnum.MAGIC, 18F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void beforeBehavior(BattleRoundDTO battleRound) {
                    BuffUtil.addBuff(battleRound.getFrom(), battleRound.getFrom(),
                            new BuffDTO(EffectTypeEnum.HIT, "", 999999999L),
                            0, battleRound.getBuilder());
                    BuffUtil.addBuff(battleRound.getFrom(), battleRound.getFrom(),
                            new BuffDTO(EffectTypeEnum.MAGIC_PENETRATE, "", 0.15F),
                            0, battleRound.getBuilder());
                }

                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                            .from(battleEffect.getFrom()).tar(battleEffect.getTar()).round(2)
                            .builder(battleEffect.getBattleRound().getBuilder()).build();
                    AbnormalEnum.ABSENT_MINDED.doEffect(input);
                }
            }
    ),

    SKILL_40(40L, "热核弹头/强能冲袭",
            "枪炮师装载热核弹头，出手前提高自身15%穿透1回合，并造成450%物理伤害。伪神姿态下改为提升自身30%穿透持续1回合，并造成600%物理伤害",
            JobEnum.GUN.getJobCode(), 35L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 4.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void beforeBehavior(BattleRoundDTO battleRound) {
                    BattleDTO from = battleRound.getFrom();
                    float rate = from.getMarkMap().containsKey("伪神") ? 0.3F : 0.15F;
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.PENETRATE, "", rate), 0,
                            battleRound.getBuilder());
                }

                @Override
                public void changeDamageRate(BattleEffectDTO battleEffect) {
                    if (battleEffect.getFrom().getMarkMap().containsKey("伪神")) {
                        battleEffect.setDamageRate(BigDecimal.valueOf(6));
                    }
                }
            }
    ) {
        @Override
        public String getName(BattleRoundDTO battleRound) {
            return battleRound.getFrom().getMarkMap().containsKey("伪神") ? "强能冲袭" : "热核弹头";
        }
    },

    SKILL_41(41L, "虚灵魅影/神像化虚",
            "提升自身30%速度 40%闪避持续三回合。伪神姿态下改为提升自身50%速度，70%闪避持续三回合",
            JobEnum.GUN.getJobCode(), 50L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    float speedRate = from.getMarkMap().containsKey("伪神") ? 0.5F : 0.3F;
                    float dodgeRate = from.getMarkMap().containsKey("伪神") ? 0.7F : 0.4F;
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.SPEED, "", speedRate), 3, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.DODGE, "", dodgeRate), 3, builder);
                }
            }
    ) {
        @Override
        public String getName(BattleRoundDTO battleRound) {
            return battleRound.getFrom().getMarkMap().containsKey("伪神") ? "神像化虚" : "虚灵魅影";
        }
    },

    SKILL_42(42L, "斩魂",
            "造成500%物理伤害，命中后降低敌方当前最大血量5%的血量上限",
            JobEnum.EVIL.getJobCode(), 45L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO tar = battleEffect.getTar();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    long maxHp = tar.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition();
                    long maxHpDecrease = Math.min(Math.round(maxHp * 0.05), 100000);
                    builder.append("，").append(tar.getOrganismInfoDTO().getOrganismDTO().getName()).append("血量上限降低").append(maxHpDecrease);
                    tar.getOrganismInfoDTO().getOrganismDTO().setMaxHpWithAddition(maxHp - maxHpDecrease);
                }
            }
    ),

    SKILL_43(43L, "血戮",
            "提升自身40%攻击，30%吸血，持续4回合",
            JobEnum.EVIL.getJobCode(), 50L,
            new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.ATK, "", 0.4F), 4, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.LIFE_STEAL, "", 0.3F), 4, builder);
                }
            }
    ),

    SKILL_44(44L, "贪慑",
            "提升自身50%吸血持续1回合，震慑敌方，命中后使敌方眩晕1回合，对敌方造成800%物理伤害",
            JobEnum.EVIL.getJobCode(), 70L,
            Arrays.asList(
                    new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F,
                            new ThisEffectExtraBattleProcessTemplate() {
                                @Override
                                public void ifHit(BattleEffectDTO battleEffect) {
                                    BattleDTO from = battleEffect.getFrom();
                                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.LIFE_STEAL, "", 0.5F),
                                            1, builder);
                                }
                            }),
                    new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 8F,
                            new ThisEffectExtraBattleProcessTemplate() {
                                @Override
                                public void ifHit(BattleEffectDTO battleEffect) {
                                    BattleDTO from = battleEffect.getFrom();
                                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.PAUSE, "眩晕", 0.5F),
                                            1, builder);
                                }
                            })
            )
    ),

    SKILL_45(45L, "神恸",
            "清除自身所有异常状态与弱化效果。受到致死攻击时免除死亡并回复血量上限60%的血量，每场战斗限一次",
            JobEnum.EVIL.getJobCode(), 70L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.clearAbnormalBuff(from, builder);
                    BuffUtil.clearInactiveBuff(from, builder);
                }
            },
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeMeDeath(BattleEffectDTO battleEffect) {
                    if (!this.getOwner().getMarkMap().containsKey("神恸")) {
                        this.getOwner().getMarkMap().put("神恸", 1);
                        battleEffect.getDamage().set(0L);
                        OrganismDTO o = this.getOwner().getOrganismInfoDTO().getOrganismDTO();
                        BattleUtil.doHealing(this.getOwner(), Math.round(0.6 * o.getMaxHpWithAddition()),
                                battleEffect.getBattleRound().getBuilder(), battleEffect.getBattleRound().getBattleField());
                    }
                }
            }
    ),

    SKILL_46(46L, "灾祭",
            "自身行动20回合后战斗仍未结束，降低自身70%血量上限，并且提升200%攻击与40%吸血",
            JobEnum.EVIL.getJobCode(), false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMyRound(BattleRoundDTO battleRound) {
            if (this.getOwner().getRound() >= 20 && !this.getOwner().getMarkMap().containsKey("灾祭")) {
                this.getOwner().getMarkMap().put("灾祭", 1);
                OrganismDTO organism = this.getOwner().getOrganismInfoDTO().getOrganismDTO();
                StringBuilder builder = new StringBuilder("※").append(organism.getName())
                        .append("的被动【灾祭】被触发");
                long newMaxHp = Math.round(organism.getMaxHpWithAddition() * 0.7);
                builder.append("，最大血量减少").append(organism.getMaxHpWithAddition() - newMaxHp).append("点");
                organism.setMaxHpWithAddition(newMaxHp);
                // 重算超过上限的血量
                organism.setHpWithAddition(organism.getHpWithAddition());
                BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                        new BuffDTO(EffectTypeEnum.ATK, "", 2F), 1000, builder);
                BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                        new BuffDTO(EffectTypeEnum.LIFE_STEAL, "", 0.4F), 1000, builder);
                battleRound.getBattleField().getBattleMsg().add(builder.toString());
            }
        }
    }),

    SKILL_47(47L, "灵融机芯",
            "激活灵融机芯，解放伪神姿态，伪神姿态下枪炮师每回合行动前降低自身25点蓝量，伪神姿态持续五回合，" +
                    "伪神姿态持续时间内再次使用灵融机芯可提前结束伪神姿态。蓝量不足25点自动退出伪神姿态。",
            JobEnum.GUN.getJobCode(), 40L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    if (from.getMarkMap().containsKey("伪神")) {
                        from.getMarkMap().remove("伪神");
                        builder.append("，退出伪神姿态");
                    } else {
                        from.getMarkMap().put("伪神", 5);
                        builder.append("，进入伪神姿态");
                    }
                }
            },
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeMyRound(BattleRoundDTO battleRound) {
                    if (!this.getOwner().getMarkMap().containsKey("伪神")) {
                        return;
                    }
                    OrganismInfoDTO info = this.getOwner().getOrganismInfoDTO();
                    if (info.getOrganismDTO().getMpWithAddition() < 25) {
                        this.getOwner().getMarkMap().remove("伪神");
                        battleRound.getBattleField().getBattleMsg()
                                .add("※" + info.getOrganismDTO().getName() + "的蓝量不足，退出伪神姿态");
                        return;
                    }
                    StringBuilder builder = new StringBuilder();
                    BattleUtil.doMpChange(this.getOwner(), -25, builder);
                    this.getOwner().getMarkMap().put("伪神", this.getOwner().getMarkMap().get("伪神") - 1);
                }

                @Override
                public void afterMyRound(BattleRoundDTO battleRound) {
                    if (!this.getOwner().getMarkMap().containsKey("伪神")) {
                        return;
                    }
                    if (this.getOwner().getMarkMap().get("伪神") == 0) {
                        this.getOwner().getMarkMap().remove("伪神");
                        battleRound.getBattleField().getBattleMsg()
                                .add("※" + this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName() + "退出伪神姿态");
                    }
                }
            }
    ),

    SKILL_48(48L, "机灵自愈/众生虚妙",
            "每回合回复自身2%血量4%蓝量持续三回合。伪神姿态下每回合回复全体友方4%血量,8%蓝量持续三回合",
            JobEnum.GUN.getJobCode(), 120L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    battleEffect.getFrom().getMarkMap().put("机灵自愈/众生虚妙", 3);
                }
            }, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMyRound(BattleRoundDTO battleRound) {
            if (!this.getOwner().getMarkMap().containsKey("机灵自愈/众生虚妙")) {
                return;
            }
            int cnt = this.getOwner().getMarkMap().get("机灵自愈/众生虚妙");
            --cnt;
            if (cnt <= 0) {
                this.getOwner().getMarkMap().remove("机灵自愈/众生虚妙");
            }
            float healRate = this.getOwner().getMarkMap().containsKey("伪神") ? 0.04F : 0.02F;
            float mpRate = this.getOwner().getMarkMap().containsKey("伪神") ? 0.08F : 0.04F;

            List<BattleDTO> skillTars = this.getOwner().getMarkMap().containsKey("伪神") ?
                    Collections.singletonList(battleRound.getFrom()) : battleRound.getOur();
            skillTars.forEach(b -> {
                String skillName = this.getOwner().getMarkMap().containsKey("伪神") ? "众生虚妙" : "机灵自愈";
                long heal = Math.round(healRate * b.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition());
                long mp = Math.round(mpRate * b.getOrganismInfoDTO().getOrganismDTO().getMaxMpWithAddition());
                battleRound.getBuilder().append(b.getOrganismInfoDTO().getOrganismDTO().getName())
                        .append("的技能效果【").append(skillName).append("】被触发");
                BattleUtil.doHealing(b, heal, battleRound.getBuilder(), battleRound.getBattleField());
                BattleUtil.doMpChange(b, mp, battleRound.getBuilder());
            });
        }
    }
    ) {
        @Override
        public String getName(BattleRoundDTO battleRound) {
            return battleRound.getFrom().getMarkMap().containsKey("伪神") ? "众生虚妙" : "机灵自愈";
        }
    },

    SKILL_49(49L, "卫星锁定/天终湮灭",
            "锁定目标施展湮灭法则，下回合开始时对敌方造成500%攻击力的真实伤害。伪神姿态下造成800%攻击力的真实伤害，" +
                    "同时使敌方进入“焚毁”异常持续一回合",
            JobEnum.GUN.getJobCode(), 160L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO tar = battleEffect.getTar();
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    tar.getMarkMap().put("湮灭", 1);
                    AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                            .from(from).tar(tar).round(1).builder(builder).build();
                    AbnormalEnum.BURN_DOWN.doEffect(input);
                }
            },
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeMyRound(BattleRoundDTO battleRound) {
                    battleRound.getEnemy().stream().filter(e -> e.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0)
                            .filter(e -> e.getMarkMap().containsKey("湮灭"))
                            .forEach(e -> {
                                e.getMarkMap().remove("湮灭");
                                StringBuilder builder = new StringBuilder("※")
                                        .append(e.getOrganismInfoDTO().getOrganismDTO().getName())
                                        .append("受湮灭法则影响");
                                int damageRate = e.getMarkMap().containsKey("伪神") ? 8 : 5;
                                long atk = BattleUtil.getLongProperty(
                                        this.getOwner().getOrganismInfoDTO().getOrganismDTO().getAtk(),
                                        OrganismPropertiesEnum.ATK.getFieldName(),
                                        this.getOwner(), battleRound.getBattleField());
                                BattleUtil.doRealDamage(e, atk * damageRate, builder);
                                battleRound.getBattleField().getBattleMsg().add(builder.toString());
                            });
                }
            }
    ) {
        @Override
        public String getName(BattleRoundDTO battleRound) {
            return battleRound.getFrom().getMarkMap().containsKey("伪神") ? "天终湮灭" : "卫星锁定";
        }
    },

    SKILL_50(50L, "秽斩·倒吊",
            "对敌方造成700%物理伤害。对战世界boss“异维生命体·冥水河神·M10”时造成双倍伤害，并破除其“静水流深”状态。" +
                    "释放后自身攻击与防御降低15%持续三回合",
            Constants.ALL, 110L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 7F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.ATK, -0.15F), 3, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.DEF, -0.15F), 3, builder);
                }
            }
    ),

    SKILL_51(51L, "秽斩·天锋",
            "对敌方造成2100%物理伤害。对战世界boss“异维生命体·冥水河神·M10”时造成双倍伤害，并破除其“静水流深”状态，同时获得十层“薪火”",
            Constants.ALL, 350L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 21F)
    ),

    SKILL_52(52L, "秽斩·星神",
            "对敌方造成2100%法术伤害。对战世界boss“异维生命体·冥水河神·M10”时造成双倍伤害，并破除其“静水流深”状态，同时获得十层“薪火”",
            Constants.ALL, 350L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 21F)
    ),

    SKILL_53(53L, "秽斩·卦术",
            "对敌方造成700%法术伤害。对战世界boss“异维生命体·冥水河神·M10”时造成双倍伤害，并破除其“静水流深”状态。" +
                    "释放后自身速度与法抗降低15%持续三回合。",
            Constants.ALL, 110L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 7F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.SPEED, -0.15F), 3, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.MAGIC_DEF, -0.15F), 3, builder);
                }
            }
    ),

    SKILL_54(54L, "破障迷蔼之光", "降低友方全体2点“血之花”成熟度，回复友方全体5000+100%法强点血量", Constants.ALL, 120L,
            new SkillEffectDTO(TargetEnum.ALL_OUR), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void ifHit(BattleEffectDTO battleEffect) {
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            BattleFieldDTO battleField = battleEffect.getBattleRound().getBattleField();

            changedBloodFlower(battleEffect.getTar(), -2, builder);
            long src = battleEffect.getFrom().getOrganismInfoDTO().getOrganismDTO().getMagicAtk();
            long magicAtk = BattleUtil.getLongProperty(src, OrganismPropertiesEnum.MAGIC_ATK.getFieldName(),
                    battleEffect.getFrom(), battleField);
            BattleUtil.doHealing(battleEffect.getTar(), 5000 + magicAtk, builder, battleField);
        }
    }),

    SKILL_55(55L, "破障迷蔼之锋", "降低友方全体2点“血之花”成熟度，对敌方单体造成1500%物理伤害，对血魔额外造成1000%物理伤害", Constants.ALL, 120L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 15F), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void afterEffect(BattleEffectDTO battleEffect) {
            battleEffect.getOur().forEach(o -> changedBloodFlower(o, -2, battleEffect.getBattleRound().getBuilder()));
        }
    }),

    SKILL_56(56L, "碎障之光", "降低友方全体1点“血之花”成熟度，回复友方全体50%法强点血量。自身造成伤害减少25%持续一回合",
            Constants.ALL, 60L, new SkillEffectDTO(TargetEnum.ME), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void ifHit(BattleEffectDTO battleEffect) {
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            long magicAtk = BattleUtil.getLongProperty(
                    battleEffect.getFrom().getOrganismInfoDTO().getOrganismDTO().getMagicAtk(),
                    OrganismPropertiesEnum.MAGIC_ATK.getFieldName(), battleEffect.getFrom(),
                    battleEffect.getBattleRound().getBattleField());
            battleEffect.getOur().forEach(o -> {
                changedBloodFlower(o, -1, builder);
                BattleUtil.doHealing(o, magicAtk, builder, battleEffect.getBattleRound().getBattleField());
            });
            BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(), new BuffDTO(EffectTypeEnum.DO_DAMAGE, -0.25F), 1, builder);
        }
    }),

    SKILL_57(57L, "破障之锋", "造成1000%物理伤害，降低友方全体1点“血之花”成熟度，自身防御降低25%持续一回合",
            Constants.ALL, 60L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 10F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    battleEffect.getOur().forEach(o -> changedBloodFlower(o, -1, builder));
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(), new BuffDTO(EffectTypeEnum.DEF, -0.25F), 1, builder);
                }
            }
    ),

    SKILL_58(58L, "星启·临河倾渊",
            "造成1700%法术伤害，对血魔造成伤害翻倍。血魔第二姿态时，额外削减血魔两层“血之华”并转化为“血之花”成熟度（无血之华则不转化）",
            Constants.ALL, 200L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 17F)
    ),

    SKILL_59(59L, "绝锋·天孤破云",
            "造成1700%物理伤害，对血魔额外造成1000%物理伤害并使血魔防御降低15%持续三回合。血魔第二姿态时额外削减血魔两层“血之华”并转化为“血之花”成熟度（无血之华则不转化）",
            Constants.ALL, 200L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 17F)
    ),


    /**
     * 以下是BOSS的技能
     **/

    SKILL_1000(1000L, "雷击",
            "天劫的基础手段，造成100%物理伤害",
            "None", new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F)
    ),

//    SKILL_1001(1001L, "天雷化身-剑",
//            "天劫化身为剑，造成50%物理伤害，并提升5%攻击，持续5回合",
//            "None", 10L,
//            Arrays.asList(
//                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.8F),
//                    new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
//                            new BuffDTO(BuffTypeEnum.ATK, "", 0.05F, 5, 1F)
//                    )
//            )
//    ),

//    SKILL_1002(1002L, "天威",
//            "天劫释放天威，降低对方10%攻击和10%法强，持续5回合",
//            "None", 20L,
//            new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0F,
//                    Arrays.asList(
//                            new BuffDTO(BuffTypeEnum.ATK, "", -0.1F, 5),
//                            new BuffDTO(BuffTypeEnum.MAGIC_ATK, "", -0.1F, 5)
//                    )
//            )
//    ),

    SKILL_1003(1003L, "五雷轰顶",
            "天劫释放五道天雷，分别造成150%，180%，220%，300%，350%物理伤害",
            "None", 100L,
            Arrays.asList(
                    new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.5F),
                    new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.8F),
                    new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 2.2F),
                    new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 3F),
                    new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 3.5F)
            )
    ),

//    SKILL_1004(1004L, "雷霆万钧",
//            "雷霆之所击，无不摧折者；万钧之所压，无不糜灭者。造成自身攻击1000%的真实伤害，并降低对方10%的攻击、法强、防御、法抗、速度、" +
//                    "命中、闪避、暴击、抗暴、爆伤、爆免，持续5回合",
//            "None", 1000L,
//            Collections.singletonList(
//                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.SPECIAL, 10F,
//                            Arrays.asList(
//                                    new BuffDTO(BuffTypeEnum.ATK, "", -0.1F, 5, 1F),
//                                    new BuffDTO(BuffTypeEnum.DEF, "", -0.1F, 5, 1F),
//                                    new BuffDTO(BuffTypeEnum.MAGIC_ATK, "", -0.1F, 5, 1F),
//                                    new BuffDTO(BuffTypeEnum.MAGIC_DEF, "", -0.1F, 5, 1F),
//                                    new BuffDTO(BuffTypeEnum.SPEED, "", -0.1F, 5, 1F),
//                                    new BuffDTO(BuffTypeEnum.HIT, "", -0.1F, 5, 1F),
//                                    new BuffDTO(BuffTypeEnum.DODGE, "", -0.1F, 5, 1F),
//                                    new BuffDTO(BuffTypeEnum.CRITICAL, "", -0.1F, 5, 1F),
//                                    new BuffDTO(BuffTypeEnum.CRITICAL_REDUCTION, "", -0.1F, 5, 1F),
//                                    new BuffDTO(BuffTypeEnum.CRITICAL_DAMAGE, "", -0.1F, 5, 1F),
//                                    new BuffDTO(BuffTypeEnum.CRITICAL_DAMAGE_REDUCTION, "", -0.1F, 5, 1F)
//                            )
//                    )
//            )
//    ),

//    SKILL_1005(1005L, "天雷化身-盾",
//            "天劫化身为盾，造成50%物理伤害，并提升10%防御和10%法抗，持续5回合",
//            "None", 10L,
//            Arrays.asList(
//                    new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F),
//                    new SkillEffectDTO(SkillTargetEnum.ME, DamageTypeEnum.ATK, 0F,
//                            Arrays.asList(
//                                    new BuffDTO(BuffTypeEnum.DEF, "", 0.1F, 5),
//                                    new BuffDTO(BuffTypeEnum.DEF, "", 0.1F, 5)
//                            )
//                    )
//            )
//    ),

    SKILL_1008(1008L, "虫殖",
            "造成伤害后获得一层虫殖印记，每层虫殖印记将为其增加2%双攻 ，5%吸血，虫殖印记数量最大为10",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void ifOurHit(BattleEffectDTO battleEffect) {
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            if (battleEffect.getDamage().get() > 0) {
                Map<String, Integer> markMap = this.getOwner().getMarkMap();
                int cnt = markMap.getOrDefault("虫殖", 0);
                int newCnt = cnt >= 10 ? cnt : cnt + 1;
                markMap.put("虫殖", newCnt);

                BuffDTO atkBuff = new BuffDTO(EffectTypeEnum.ATK, "虫殖", 0.02F * newCnt);
                BuffDTO magicAtkBuff = new BuffDTO(EffectTypeEnum.MAGIC_ATK, "虫殖", 0.02F * newCnt);
                BuffDTO lifeStealBuff = new BuffDTO(EffectTypeEnum.LIFE_STEAL, "虫殖", 0.05F * newCnt);

                BuffUtil.addBuff(this.getOwner(), this.getOwner(), atkBuff, 1000, builder);
                BuffUtil.addBuff(this.getOwner(), this.getOwner(), magicAtkBuff, 1000, builder);
                BuffUtil.addBuff(this.getOwner(), this.getOwner(), lifeStealBuff, 1000, builder);
            }
        }

        @Override
        public Integer getPriority() {
            return 1000;
        }
    }),

    SKILL_1009(1009L, "虫噬",
            "对敌方造成（50%+10%虫殖印记）物理攻击伤害",
            "None", new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void changeDamageRate(BattleEffectDTO battleEffect) {
                    int cnt = battleEffect.getFrom().getMarkMap().getOrDefault("虫殖", 0);
                    battleEffect.setDamageRate(
                            battleEffect.getDamageRate().add(BigDecimal.valueOf(0.1).multiply(BigDecimal.valueOf(cnt)))
                    );
                }
            }
    ),

    SKILL_1010(1010L, "蝗灭",
            "消耗自身15%生命值，造成（300%+50%虫殖印记）物理伤害。当前回合结束时，虫殖数量减半",
            "None", new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 3F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void beforeEffect(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    OrganismDTO thisOrganism = from.getOrganismInfoDTO().getOrganismDTO();
                    long newHp = Math.round(thisOrganism.getHpWithAddition() * 0.85);
                    builder.append("，").append(from.getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("生命值减少了").append(thisOrganism.getHpWithAddition() - newHp);
                    thisOrganism.setHpWithAddition(newHp);
                }

                @Override
                public void changeDamageRate(BattleEffectDTO battleEffect) {
                    int cnt = battleEffect.getFrom().getMarkMap().getOrDefault("虫殖", 0);
                    battleEffect.setDamageRate(
                            battleEffect.getDamageRate().add(BigDecimal.valueOf(0.5).multiply(BigDecimal.valueOf(cnt)))
                    );
                }

                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
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
        public void beforeBattle(BattleFieldDTO battleField) {
            String msg = "※" + this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName() + "的被动【摩诃无量（残）】被触发";
            StringBuilder builder = new StringBuilder(msg);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.ATK, "摩诃无量", 0.3F,
                    BuffOverrideStrategyEnum.DEPENDS_ON_LEVEL, 1), 3, builder);
            battleField.getBattleMsg().add(builder.toString());
        }
    }),

    SKILL_1012(1012L, "斩",
            "造成110%物理伤害",
            "None", new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.1F)
    ),

    SKILL_1013(1013L, "星术·珠明",
            "对敌方造成80%物理伤害",
            "None", new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.8F)
    ),

    SKILL_1014(1014L, "星术·魂养",
            "使用时回复自身30%蓝量",
            "None", 300L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
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
            "None", 1000L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    OrganismDTO fromOrganismDTO = from.getOrganismInfoDTO().getOrganismDTO();
                    long magicAtk = BattleUtil.getLongProperty(fromOrganismDTO.getMagicAtk(),
                            EffectTypeEnum.MAGIC_ATK.getName(), from, battleEffect.getBattleRound().getBattleField());
                    BattleUtil.doHealing(from, Math.round(magicAtk * 1.3F), builder,
                            battleEffect.getBattleRound().getBattleField());
                }
            }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_1016(1016L, "星术·嗔魂",
            "提高自身20%吸血持续2回合",
            "None", 300L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.LIFE_STEAL, "", 0.2F), 2, builder);
                }
            }
    ),

    SKILL_1017(1017L, "星术·灭神",
            "对敌方造成1550%物理伤害",
            "None", 1000L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 15.5F)
    ),

    SKILL_1018(1018L, "斫霜",
            "攻击造成伤害后50%概率降低敌方30%速度持续一回合。该效果未触发则降低敌方2%血量上限。",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void ifOurHit(BattleEffectDTO battleEffect) {
            if (battleEffect.getDamage().get() > 0) {
                BattleDTO tar = battleEffect.getTar();
                BattleDTO from = battleEffect.getFrom();
                StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                boolean effect;
                if (this.getOwner().getMarkMap().containsKey(SKILL_1021.getName() + "-斫霜")) {
                    effect = true;
                    this.getOwner().getMarkMap().remove(SKILL_1021.getName() + "-斫霜");
                } else {
                    effect = RandomUtil.isHit(0.5F);
                }
                if (effect) {
                    BuffUtil.addBuff(from, tar, new BuffDTO(EffectTypeEnum.SPEED, "斫霜", -0.3F), 1, builder);
                } else {
                    OrganismDTO tarOrganism = tar.getOrganismInfoDTO().getOrganismDTO();
                    tarOrganism.setMaxHpWithAddition(NumberUtil.multiply(tarOrganism.getMaxHpWithAddition(), 0.98F));
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
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.8F),
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 0.3F)
    )),

    SKILL_1020(1020L, "莹雪化谢",
            "造成300%法术+150%物理伤害，若造成伤害后对敌方造成“斫霜”的速度弱化状态，则在回合结束时使敌方冻结持续一回合",
            "None", 50L, Arrays.asList(
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.5F),
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 3F)
    ), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void ifHit(BattleEffectDTO battleEffect) {
            if (battleEffect.getDamage().get() > 0) {
                List<BattleBuffDTO> buffList = battleEffect.getTar().getBuffMap().get(EffectTypeEnum.SPEED);
                if (CollectionUtil.isNotEmpty(buffList)
                        && buffList.stream().anyMatch(buff -> "斫霜".equals(buff.getBuffDTO().getBuffName()))) {
                    battleEffect.getTar().getMarkMap().put("莹雪化谢-斫霜", 1);
                }
            }
        }

        @Override
        public void afterBehavior(BattleRoundDTO battleRound) {
            StringBuilder builder = battleRound.getBuilder();
            battleRound.getEnemy().stream().filter(e -> e.getMarkMap().containsKey("莹雪化谢-斫霜")).forEach(e -> {
                e.getMarkMap().remove("莹雪化谢-斫霜");
                BuffUtil.addBuff(battleRound.getFrom(), e,
                        new BuffDTO(EffectTypeEnum.PAUSE, "冻结", true), 1, builder);
            });
        }
    }),

    SKILL_1021(1021L, "精铁寒利",
            "提升自身30%攻击30%法强持续三回合，下一次攻击造成伤害后的斫霜的速度降低效果必定触发",
            "None", 75L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.ATK, "", 0.3F), 3, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.MAGIC_ATK, "", 0.3F), 3, builder);
                    battleEffect.getFrom().getMarkMap().put("精铁寒利-斫霜", 1);
                }
            }),

    SKILL_1022(1022L, "昙月见景",
            "造成900%物理+300%法术伤害。召唤昙月持续三回合，昙月回合内星神免疫异常状态，昙月每回合追加攻击，造成星神80%法术伤害，" +
                    "追加攻击造成伤害后触发斫霜效果",
            "None", 150L, Arrays.asList(
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 9F),
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 3F)
    ), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void afterEffect(BattleEffectDTO battleEffect) {
            BattleDTO from = battleEffect.getFrom();
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            BuffUtil.clearAbnormalBuff(from, builder);
            from.getMarkMap().put("昙月", 3);
            BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.IMMUNITY, "", 0L), 3, builder);
        }
    }, new ExtraBattleProcessTemplate() {
        @Override
        public void afterMyBehavior(BattleEffectDTO battleEffect) {
            Map<String, Integer> markMap = this.getOwner().getMarkMap();
            if (markMap.containsKey("昙月")) {
                ExtraBattleProcessTemplate process = CopyUtil.copyNewInstance(SKILL_1018.globalExtraProcess);
                process.setOwner(this.getOwner());

                BattleRoundDTO battleRound = battleEffect.getBattleRound();
                BattleEffectDTO extraBattleEffect = BattleEffectDTO.builder()
                        .nowSkill(SKILL_1023).skillEffect(SKILL_1023.skillEffects.get(0))
                        .damageRate(BigDecimal.valueOf(SKILL_1023.skillEffects.get(0).getDamageRate()))
                        .battleRound(battleRound)
                        .from(this.getOwner()).tar(battleEffect.getTar())
                        .build();
                extraBattleEffect.executeBehavior();
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
            "None", new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 0.8F)
    ),

    SKILL_1024(1024L, "摩诃无量",
            "战斗开始时提高自身60%攻击力持续六回合",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(BattleFieldDTO battleField) {
            StringBuilder builder = new StringBuilder("※")
                    .append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动【摩诃无量】被触发");
            BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.ATK, "摩诃无量", 0.6F,
                    BuffOverrideStrategyEnum.DEPENDS_ON_LEVEL, 2), 6, builder);
            battleField.getBattleMsg().add(builder.toString());
        }
    }),

    SKILL_1025(1025L, "啜疾",
            "造成130%物理伤害，若未命中，则在使摩诃无量攻击提升回合数+1（为0时仍+1）",
            "None", new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.3F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifNotHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.addBuff(from, from,
                            new BuffDTO(EffectTypeEnum.ATK, "摩诃无量", 0.6F, BuffOverrideStrategyEnum.ROUND_PLUS),
                            1, builder);
                }
            }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_1026(1026L, "世法解离",
            "脱离世法，不入轮回。造成450%物理伤害，行动开始时提升自身50%闪避持续三回合，回合结束时，使摩诃无量攻击提升回合数+3",
            "None", 80L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 4.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void beforeEffect(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.DODGE, "", 0.5F), 3, builder);
                }

                @Override
                public void afterBehavior(BattleRoundDTO battleRound) {
                    BattleDTO from = battleRound.getFrom();
                    StringBuilder builder = battleRound.getBuilder();
                    BuffUtil.addBuff(from, from,
                            new BuffDTO(EffectTypeEnum.ATK, "摩诃无量", 0.6F, BuffOverrideStrategyEnum.ROUND_PLUS),
                            3, builder);
                }
            }
    ),

    SKILL_1027(1027L, "阔海无岸",
            "每次行动后提升自身5%速度，攻击命中后降低敌方30%血量回复效果持续两回合",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void ifMeHitEnemy(BattleEffectDTO battleEffect) {
            BattleDTO tar = battleEffect.getTar();
            BattleDTO from = battleEffect.getFrom();
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            BuffUtil.addBuff(from, tar, new BuffDTO(EffectTypeEnum.HEAL, "", -0.3F), 2, builder);
        }

        @Override
        public void afterMyBehavior(BattleEffectDTO battleEffect) {
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            BuffUtil.addBuff(getOwner(), getOwner(),
                    new BuffDTO(EffectTypeEnum.SPEED, "", 0.05F), 1000, builder);
        }
    }),

    SKILL_1028(1028L, "盈覆",
            "造成120%物理伤害，若此时自身速度达到1200点，则降低为80%物理伤害",
            "None", new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.2F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void changeDamageRate(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    if (from.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed() >= 1200) {
                        battleEffect.setDamageRate(BigDecimal.valueOf(0.8F));
                    }
                }
            }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_1029(1029L, "海合净明",
            "造成400%法术伤害，若此时速度达到1200点则额外使敌方眩晕一回合",
            "None", 40L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 4F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO tar = battleEffect.getTar();
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    if (from.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed() >= 1200) {
                        BuffUtil.addBuff(from, tar,
                                new BuffDTO(EffectTypeEnum.PAUSE, "眩晕", true), 1, builder);
                    }
                }
            }
    ),

    SKILL_1030(1030L, "涤世",
            "清除敌方增益buff效果，若此时速度达到1200点，则提升自身25%攻击25%法强和25%闪避，持续三回合",
            "None", 60L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    battleEffect.getEnemy().forEach(e -> BuffUtil.clearActiveBuff(e, battleEffect.getBattleRound().getBuilder()));
                }
            }
    ),

    SKILL_1031(1031L, "天水鸣叹",
            "造成1300%法术伤害，或若此时速度超过1200点，则开启天水，降低敌方全体25%闪避，25%法抗后，造成1700%法术伤害。" +
                    "天水持续时间内己方全体造成眩晕回合数额外+1 （已被眩晕同样+1回合）天水开启持续三回合",
            "None", 180L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 13F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    if (from.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed() >= 1200) {
                        BuffUtil.addBuff(from, from,
                                new BuffDTO(EffectTypeEnum.DODGE, "", -0.25F), 3, builder);
                        BuffUtil.addBuff(from, from,
                                new BuffDTO(EffectTypeEnum.MAGIC_DEF, "", -0.25F), 3, builder);
                        battleEffect.getOur().forEach(o -> o.getMarkMap().put("天水", 3));
                    }
                }
            }, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeOurRound(BattleRoundDTO battleRound) {
            if (getOwner().getMarkMap().containsKey("天水")) {
                int round = getOwner().getMarkMap().get("天水") - 1;
                if (round < 0) {
                    getOwner().getMarkMap().remove("天水");
                } else {
                    getOwner().getMarkMap().put("天水", round);
                }
            }
        }
    }),

    SKILL_1032(1032L, "觉·摩诃无量",
            "战斗开始时提升自身100%攻击力，50%命中，50%穿甲，50%防御，50%法抗持续整场战斗，若该buff效果被清除，则五回合后重新激活" +
                    "所有摩诃无量同名效果只触发一条，且（觉>神>普>残）",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(BattleFieldDTO battleField) {
            StringBuilder builder = new StringBuilder("※");
            builder.append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName()).append("的被动【觉·摩诃无量】被触发");
            BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.ATK, "摩诃无量", 1F,
                    BuffOverrideStrategyEnum.DEPENDS_ON_LEVEL, 4), 1000, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.HIT, "摩诃无量", 0.5F,
                    BuffOverrideStrategyEnum.DEPENDS_ON_LEVEL, 4), 1000, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.PENETRATE, "摩诃无量", 0.5F,
                    BuffOverrideStrategyEnum.DEPENDS_ON_LEVEL, 4), 1000, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.DEF, "摩诃无量", 0.5F,
                    BuffOverrideStrategyEnum.DEPENDS_ON_LEVEL, 4), 1000, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.MAGIC_DEF, "摩诃无量", 0.5F,
                    BuffOverrideStrategyEnum.DEPENDS_ON_LEVEL, 4), 1000, builder);
            battleField.getBattleMsg().add(builder.toString());
        }

        @Override
        public void beforeMyRound(BattleRoundDTO battleRound) {
            Map<String, Integer> markMap = this.getOwner().getMarkMap();
            if (markMap.containsKey("摩诃无量")) {
                if (markMap.get("摩诃无量") <= 1) {
                    markMap.remove("摩诃无量");
                    StringBuilder builder = new StringBuilder("※");
                    builder.append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName()).append("的被动【觉·摩诃无量】被触发");
                    BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.ATK, "摩诃无量", 1F,
                            BuffOverrideStrategyEnum.DEPENDS_ON_LEVEL, 4), 1000, builder);
                    BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.HIT, "摩诃无量", 0.5F,
                            BuffOverrideStrategyEnum.DEPENDS_ON_LEVEL, 4), 1000, builder);
                    BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.PENETRATE, "摩诃无量", 0.5F,
                            BuffOverrideStrategyEnum.DEPENDS_ON_LEVEL, 4), 1000, builder);
                    BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.DEF, "摩诃无量", 0.5F,
                            BuffOverrideStrategyEnum.DEPENDS_ON_LEVEL, 4), 1000, builder);
                    BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.MAGIC_DEF, "摩诃无量", 0.5F,
                            BuffOverrideStrategyEnum.DEPENDS_ON_LEVEL, 4), 1000, builder);
                    battleRound.getBattleField().getBattleMsg().add(builder.toString());
                } else {
                    markMap.put("摩诃无量", markMap.get("摩诃无量") - 1);
                }
            } else {
                List<BattleBuffDTO> buffList = this.getOwner().getBuffMap().get(EffectTypeEnum.ATK);
                if (buffList.stream().noneMatch(buff -> "摩诃无量".equals(buff.getBuffDTO().getBuffName()))) {
                    markMap.put("摩诃无量", 5);
                }
            }
        }
    }),

    SKILL_1033(1033L, "魔慑·汲魂",
            "造成120%物理伤害同时从中汲取生命力，使此攻击附带20%物理吸血，每次攻击造成伤害时使敌方防御降低2%持续一回合，" +
                    "每使用一次该防御降低效果增加2%，最多不超过50%",
            "None", new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.2F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    BattleDTO tar = battleEffect.getTar();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();

                    BattleUtil.doHealing(from, Math.round(0.2F * battleEffect.getDamage().get()), builder,
                            battleEffect.getBattleRound().getBattleField());
                    int cnt = from.getMarkMap().getOrDefault("魔慑·汲魂", 0) + 1;
                    cnt = Math.min(cnt, 25);
                    from.getMarkMap().put("魔慑·汲魂", cnt);
                    BuffUtil.addBuff(from, tar, new BuffDTO(EffectTypeEnum.DEF, "", -0.02F * cnt), 1, builder);
                    BattleUtil.doHealing(from, Math.round(battleEffect.getDamage().get() * 0.2F), builder,
                            battleEffect.getBattleRound().getBattleField());
                }
            }, new ExtraBattleProcessTemplate() {
    }),

    SKILL_1034(1034L, "伪.泯灭终释",
            "清除自身所有增益与弱化效果后对敌方造成800%攻击力的真实伤害",
            "None", new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO tar = battleEffect.getTar();
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.clearActiveBuff(from, builder);
                    BuffUtil.clearAbnormalBuff(from, builder);
                    BattleUtil.doRealDamage(tar, 8 * from.getOrganismInfoDTO().getOrganismDTO().getAtk(), builder);
                }
            }
    ),

    SKILL_1035(1035L, "“勇者”（初）",
            "星神认可之“勇者”，战斗开始时，双攻，双防，闪避，命中，速度全部提升10%持续十回合，并附加对敌方1号位敌人造成15%法抗下降的法则效果",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(BattleFieldDTO battleField) {
            StringBuilder builder = new StringBuilder("※");
            builder.append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName()).append("的被动【“勇者”（初）】被触发");
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.ATK, "“勇者”（初）", 0.1F), 10, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.MAGIC_ATK, "“勇者”（初）", 0.1F), 10, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.DEF, "“勇者”（初）", 0.1F), 10, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.MAGIC_DEF, "“勇者”（初）", 0.1F), 10, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.HIT, "“勇者”（初）", 0.1F), 10, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.DODGE, "“勇者”（初）", 0.1F), 10, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.SPEED, "“勇者”（初）", 0.1F), 10, builder);
            RuleUtil.addRule(this.getOwner(), battleField.getEnemy(this.getOwner()).get(0),
                    EffectTypeEnum.MAGIC_DEF, "“勇者”（初）", -0.15F, builder);
            battleField.getBattleMsg().add(builder.toString());
        }
    }),

    SKILL_1036(1036L, "勇者之剑",
            "造成100%物理+40%法术伤害",
            "None", Arrays.asList(
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F),
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 0.4F)
    )),

    SKILL_1037(1037L, "蒙星天赐",
            "提升自身20%法强持续三回合，对敌方造成100%物理+350%法术伤害",
            "None", 100L, Arrays.asList(
            new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.MAGIC, 0F,
                    new ThisEffectExtraBattleProcessTemplate() {
                        @Override
                        public void ifHit(BattleEffectDTO battleEffect) {
                            BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                                    new BuffDTO(EffectTypeEnum.MAGIC_ATK, "", 0.2F), 3,
                                    battleEffect.getBattleRound().getBuilder());
                        }
                    }
            ),
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F),
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 3.5F)
    )),

    SKILL_1038(1038L, "年兽之力",
            "年兽的特殊能力，战斗开始时，当对方无人装备“轰天雷”时，属性特大幅提升",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(BattleFieldDTO battleField) {
            OrganismDTO organism = this.getOwner().getOrganismInfoDTO().getOrganismDTO();
            StringBuilder builder = new StringBuilder("※");
            if (battleField.getEnemy(this.getOwner()).stream()
                    .map(BattleDTO::getOrganismInfoDTO)
                    .map(OrganismInfoDTO::getEquipmentBarDTO)
                    .noneMatch(bar -> Objects.nonNull(bar)
                            && EquipmentEnum.EQUIPMENT_8.getId().equals(bar.getWeapon().getEquipmentId()))
            ) {
                builder.append(organism.getName()).append("的被动【年兽之力】被触发").append("，属性特大幅提升");
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.ATK, "年兽之力", 88.88F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.MAGIC_ATK, "年兽之力", 88.88F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.DEF, "年兽之力", 88.88F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.MAGIC_DEF, "年兽之力", 88.88F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.HIT, "年兽之力", 88.88F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.DODGE, "年兽之力", 88.88F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.SPEED, "年兽之力", 88.88F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.CRITICAL, "年兽之力", 0.88F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.CRITICAL_DAMAGE, "年兽之力", 8.8F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.CRITICAL_REDUCTION, "年兽之力", 0.88F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.CRITICAL_DAMAGE_REDUCTION, "年兽之力", 0.88F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.LIFE_STEAL, "年兽之力", 8.88F, builder);
            } else {
                builder.append("因对方装备了“轰天雷”，").append(organism.getName()).append("的【年兽之力】失效");
            }
            battleField.getBattleMsg().add(builder.toString());
        }
    }),

    SKILL_1039(1039L, "审判（惩）", "提高自身50%攻击，50%法强",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(BattleFieldDTO battleField) {
            StringBuilder builder = new StringBuilder("※")
                    .append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动【审判（惩）】被触发");
            RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.ATK, "审判（惩）", 0.5F, builder);
            RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.MAGIC_ATK, "审判（惩）", 0.5F, builder);
            battleField.getBattleMsg().add(builder.toString());
        }
    }),

    SKILL_1040(1040L, "绝对公正", "自身回合开始时，清除敌我双方所有的增益和弱化类buff效果",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMyRound(BattleRoundDTO battleRound) {
            StringBuilder builder = new StringBuilder("※")
                    .append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动【绝对公正】被触发");
            battleRound.getOur().forEach(o -> {
                BuffUtil.clearActiveBuff(o, builder);
                BuffUtil.clearInactiveBuff(o, builder);
            });
            battleRound.getEnemy().forEach(o -> {
                BuffUtil.clearActiveBuff(o, builder);
                BuffUtil.clearInactiveBuff(o, builder);
            });
            battleRound.getBattleField().getBattleMsg().add(builder.toString());
        }
    }),

    SKILL_1041(1041L, "镰斩", "造成150%物理伤害，命中后降低自身10%防御与法抗持续两回合",
            "None", new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.DEF, "", -0.1F), 2, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.MAGIC_DEF, "", -0.1F), 2, builder);
                }
            }
    ),

    SKILL_1042(1042L, "真·勇者", "星神认可，为世界带来希望之“勇者”，战斗开始时，双攻，双防，闪避，命中，速度全部提升20%持续十回合。" +
            "对敌方1号位敌人造成10%法抗10%物抗下降的法则效果",
            "None", false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(BattleFieldDTO battleField) {
            StringBuilder builder = new StringBuilder("※");
            builder.append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName()).append("的被动【真·勇者】被触发");
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.ATK, "真·勇者", 0.2F), 10, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.MAGIC_ATK, "真·勇者", 0.2F), 10, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.DEF, "真·勇者", 0.2F), 10, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.MAGIC_DEF, "真·勇者", 0.2F), 10, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.HIT, "真·勇者", 0.2F), 10, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.DODGE, "真·勇者", 0.2F), 10, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.SPEED, "真·勇者", 0.2F), 10, builder);
            RuleUtil.addRule(this.getOwner(), battleField.getEnemy(this.getOwner()).get(0),
                    EffectTypeEnum.DEF, "真·勇者", -0.1F, builder);
            RuleUtil.addRule(this.getOwner(), battleField.getEnemy(this.getOwner()).get(0),
                    EffectTypeEnum.MAGIC_DEF, "真·勇者", -0.1F, builder);
            battleField.getBattleMsg().add(builder.toString());
        }
    }),

//    SKILL_1043(1043L, "明神", "解除自身所处的异常状态与弱化类buff效果，并提升自身40%闪避持续2回合",
//            "None", 40L
//
//    ),

    SKILL_1044(1044L, "离烟", "造成550%法术伤害，命中后扣除敌方10%蓝量",
            "None", 60L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 5.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BattleDTO tar = battleEffect.getTar();
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BattleUtil.doMpChange(tar,
                            Math.round(-0.1F * tar.getOrganismInfoDTO().getOrganismDTO().getMaxMpWithAddition()), builder);
                }
            }
    ),

    SKILL_1045(1045L, "浮岸", "提升自身15%命中持续2回合，连续进行十次攻击每次攻击造成75%物理伤害",
            "None", 80L,
            Collections.nCopies(10, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.75F)),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void beforeBehavior(BattleRoundDTO battleRound) {
                    BattleDTO from = battleRound.getFrom();
                    StringBuilder builder = battleRound.getBuilder();
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.HIT, "", 0.15F), 2, builder);
                }
            }
    ),

    SKILL_1046(1046L, "祈愿", "敌我双方回复10%蓝量10%血量",
            "None", new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    battleEffect.getOur().forEach(o -> {
                        BattleUtil.doHealing(o,
                                Math.round(0.1F * o.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition()), builder,
                                battleEffect.getBattleRound().getBattleField());
                        BattleUtil.doMpChange(o,
                                Math.round(0.1F * o.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition()), builder);
                    });
                    battleEffect.getEnemy().forEach(e -> {
                        BattleUtil.doHealing(e,
                                Math.round(0.1F * e.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition()), builder,
                                battleEffect.getBattleRound().getBattleField());
                        BattleUtil.doMpChange(e,
                                Math.round(0.1F * e.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition()), builder);
                    });
                }
            }
    ),

    //    SKILL_1047(1047L, "圣裁", "对敌方造成1200%法术伤害，命中后使敌方陷入焚毁异常持续两回合",
//            "None", 130L, new SkillEffectDTO(SkillTargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 12F),
//
//            ),
    SKILL_1048(1048L, "星溟血相",
            "燃烧理智，强行榨取星神之力，每回合回复自身最大血量的8%持续三回合，提升20%双攻双防持续三回合。使用后场地效果转变为“临渊绝响”",
            Constants.NONE, 90L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void beforeBehavior(BattleRoundDTO battleRound) {
                    BattleDTO from = battleRound.getFrom();
                    StringBuilder builder = battleRound.getBuilder();
                    BattleUtil.doHealing(from,
                            Math.round(0.08F * battleRound.getFrom().getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition()),
                            builder, battleRound.getBattleField()
                    );
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.ATK, "", 0.2F), 3, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.MAGIC_ATK, "", 0.2F), 3, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.DEF, "", 0.2F), 3, builder);
                    BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.MAGIC_DEF, "", 0.2F), 3, builder);
                    battleRound.changeFieldEffect(FieldEffectEnum.EFFECT_1);
                }
            }
    ),

    SKILL_1049(1049L, "未定·星之彼岸", "使用后场地效果转换为“星之彼岸”，对敌方造成1000%法强+250%攻击点伤害",
            Constants.NONE, 150L, Arrays.asList(
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 2.5F),
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 10F),
            new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F, new ThisEffectExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    battleEffect.changeFieldEffect(FieldEffectEnum.EFFECT_2);
                }
            })
    )),

    SKILL_1050(1050L, "烟散燃尘",
            "对敌方造成伤害后，30%概率提升自身15%攻击，30%概率提升自身15%防御，30%概率提升自身15%闪避，持续两回合",
            Constants.NONE, false, new ExtraBattleProcessTemplate() {
        @Override
        public void ifMeHitEnemy(BattleEffectDTO battleEffect) {
            if (battleEffect.getDamage().get() > 0L) {
                StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                if (RandomUtil.isHit(0.3F)) {
                    BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                            new BuffDTO(EffectTypeEnum.ATK, "", 0.15F), 2, builder);
                }
                if (RandomUtil.isHit(0.3F)) {
                    BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                            new BuffDTO(EffectTypeEnum.DEF, "", 0.15F), 2, builder);
                }
                if (RandomUtil.isHit(0.3F)) {
                    BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                            new BuffDTO(EffectTypeEnum.DODGE, "", 0.15F), 2, builder);
                }
            }
        }
    }),

    SKILL_1051(1051L, "龙火燃弹", "造成450%物理伤害，命中后20%概率使敌方焚毁持续一回合",
            Constants.NONE, 50L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 4.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    if (RandomUtil.isHit(0.2F)) {
                        StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                        BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getTar(),
                                new BuffDTO(EffectTypeEnum.HEAL, "焚毁", -0.3F, true), 1, builder);
                        BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getTar(),
                                new BuffDTO(EffectTypeEnum.BLEEDING, "焚毁", -0.08F, true), 1, builder);
                    }
                }
            }
    ),

    SKILL_1052(1052L, "龙化（初）", "全属性提升25%持续五回合，对敌方造成650%物理伤害",
            Constants.NONE, 110L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 6.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.ATK, "龙化", 0.2F), 5, builder);
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.MAGIC_ATK, "龙化", 0.2F), 5, builder);
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.DEF, "龙化", 0.2F), 5, builder);
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.MAGIC_DEF, "龙化", 0.2F), 5, builder);
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.HIT, "龙化", 0.2F), 5, builder);
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.DODGE, "龙化", 0.2F), 5, builder);
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.SPEED, "龙化", 0.2F), 5, builder);
                }
            }
    ),

    SKILL_1053(1053L, "虚神劫",
            "获得虚神的加持，自身每回合开始时提升自身30%闪避持续一回合。场地效果为“虚神界”时获得“虚神量子”，单次倍率攻击附加自身当前闪避10%的真实伤害",
            Constants.NONE, false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMyRound(BattleRoundDTO battleRound) {
            StringBuilder builder = new StringBuilder("※");
            builder.append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName()).append("的被动【虚神劫】被触发");
            BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                    new BuffDTO(EffectTypeEnum.DODGE, "虚神劫", 0.3F), 1, builder);
            battleRound.getBattleField().getBattleMsg().add(builder.toString());
        }

        @Override
        public void ifMeHitEnemy(BattleEffectDTO battleEffect) {
            BattleRoundDTO battleRound = battleEffect.getBattleRound();
            if (FieldEffectEnum.EFFECT_3.equals(battleRound.getBattleField().getFieldEffectEnum())
                    && battleEffect.getDamageRate().compareTo(BigDecimal.ZERO) > 0) {
                battleRound.getBuilder().append("，“虚神量子”附加真实伤害");
                long dodge = BattleUtil.getLongProperty(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getDodge(),
                        OrganismPropertiesEnum.DODGE.getFieldName(), this.getOwner(), battleRound.getBattleField());
                BattleUtil.doRealDamage(battleEffect.getTar(), Math.round(dodge * 0.1), battleRound.getBuilder());
            }
        }
    }),

    SKILL_1054(1054L, "异数连珠", "连续对敌方造成五次100%法术伤害", Constants.NONE, 80L,
            Collections.nCopies(5, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 1F))
    ),

    SKILL_1055(1055L, "空洄摇曳", "提升自身30%速度，30%闪避持续两回合。场地为“虚神界”时改为四回合", Constants.NONE,
            110L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    int round = FieldEffectEnum.EFFECT_3.equals(
                            battleEffect.getBattleRound().getBattleField().getFieldEffectEnum()) ? 4 : 2;
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getTar(),
                            new BuffDTO(EffectTypeEnum.SPEED, 0.3F), round, battleEffect.getBattleRound().getBuilder());
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getTar(),
                            new BuffDTO(EffectTypeEnum.DODGE, 0.3F), round, battleEffect.getBattleRound().getBuilder());
                }
            }
    ),

    SKILL_1056(1056L, "魇莽归芜",
            "对敌方连续造成三次400%的法术伤害。场地为“虚神界”时额外使敌方陷入“恐惧”异常，持续一回合",
            Constants.NONE, 180L, Arrays.asList(
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 4F,
                    new ThisEffectExtraBattleProcessTemplate() {
                        @Override
                        public void ifHit(BattleEffectDTO battleEffect) {
                            if (FieldEffectEnum.EFFECT_3.equals(
                                    battleEffect.getBattleRound().getBattleField().getFieldEffectEnum())) {
                                AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                                        .from(battleEffect.getFrom()).tar(battleEffect.getTar())
                                        .round(1).builder(battleEffect.getBattleRound().getBuilder()).build();
                                AbnormalEnum.FEAR.doEffect(input);
                            }
                        }
                    }),
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 4F),
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 4F)
    )),

    SKILL_1057(1057L, "觐见·虚神的邀请", "场地效果转为“虚神界”。虚神界∶双方闪避提高40%",
            Constants.NONE, 10L, new SkillEffectDTO(TargetEnum.ME, DamageTypeEnum.ATK, 0F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    battleEffect.changeFieldEffect(FieldEffectEnum.EFFECT_3);
                }
            }
    ),

    SKILL_1058(1058L, "腐沼溺薪为之岸",
            "M10在首次受到真实伤害以外的伤害后，将对伤害来源附加“沼溺”标记，陷入“沼溺”的敌人在本场战斗内对M10造成的伤害永久降低70%，" +
                    "受到来自M10的伤害永久提升30%。“沼溺”标记于本场战斗内无法消除。除具有“沼溺”标记之外队员，" +
                    "其他队员每次在对M10造成伤害后会获得一层“薪火”。每层“薪火”使当前队员对M10造成的伤害提升2%。“薪火”效果持续整场战斗无法消除",
            Constants.NONE, false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMeDamage(BattleEffectDTO battleEffect) {
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            Map<String, Integer> fromMarkMap = battleEffect.getFrom().getMarkMap();
            if (fromMarkMap.containsKey("沼溺")) {
                builder.append("，“沼溺”使受到的伤害降低70%");
                battleEffect.getDamage().addAndGet(-Math.round(0.7F * battleEffect.getDamage().get()));
            } else {
                int fire = fromMarkMap.getOrDefault("薪火", 0);
                float rate = 0.02F * fire;
                builder.append("，“薪火”使受到的伤害提升").append(rate * 100).append("%");
                battleEffect.getDamage().addAndGet(Math.round(rate * battleEffect.getDamage().get()));
            }
        }

        @Override
        public void afterMeDamage(BattleEffectDTO battleEffect) {

            Map<String, Integer> fromMarkMap = battleEffect.getFrom().getMarkMap();
            Map<String, Integer> ownerMarkMap = this.getOwner().getMarkMap();
            if (!ownerMarkMap.containsKey("对外附加沼溺")) {
                ownerMarkMap.put("对外附加沼溺", 1);
                battleEffect.getFrom().getMarkMap().put("沼溺", 1);
            } else {
                if (!fromMarkMap.containsKey("沼溺")) {
                    fromMarkMap.put("薪火", fromMarkMap.getOrDefault("薪火", 0) + 1);
                }
            }
        }
    }),

    SKILL_1059(1059L, "神性·君临（异维）",
            "M10每段伤害附加200点真实伤害，M10攻击命中敌人后有10%概率使敌方陷入“臣服”异常持续一回合。异维入侵∶M10天生免疫全部异常效果",
            Constants.NONE, false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(BattleFieldDTO battleField) {
            BattleDTO owner = this.getOwner();
            StringBuilder builder = new StringBuilder("※").append(owner.getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动【神性·君临（异维）】被触发");
            RuleUtil.addRule(owner, owner, EffectTypeEnum.IMMUNITY, "异维入侵", 0F, builder);
            battleField.getBattleMsg().add(builder.toString());
        }

        @Override
        public void ifMeHitEnemy(BattleEffectDTO battleEffect) {
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            builder.append("，").append(battleEffect.getFrom().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动【神性·君临（异维）】被触发");
            BattleUtil.doRealDamage(battleEffect.getTar(), 200, builder);
            if (RandomUtil.isHit(0.1F)) {
                AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                        .from(battleEffect.getFrom()).tar(battleEffect.getTar())
                        .round(1).builder(battleEffect.getBattleRound().getBuilder()).build();
                AbnormalEnum.SUBMIT.doEffect(input);
            }
        }
    }),

    SKILL_1060(1060L, "唤潮咏战且为歌",
            "开局时M10进入“静水流深”状态，该状态下M10受到的全部伤害减少20%，每次释放技能后为自身附加自身最大血量8%的护盾。" +
                    "受到“秽斩”攻击后，解除“静水流深”并进入“怒意狂澜”状态，“怒意狂澜”状态下，M10造成与受到的伤害提升15%，" +
                    "对M10造成伤害的敌方单位降低80%吸血持续两回合。", Constants.NONE, false,
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeBattle(BattleFieldDTO battleField) {
                    String str = "※" + this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName() +
                            "的被动【唤潮咏战且为歌】被触发，进入“静水流深”状态";
                    this.getOwner().getMarkMap().put("静水流深", 1);
                    battleField.getBattleMsg().add(str);
                }

                @Override
                public void afterMyBehavior(BattleEffectDTO battleEffect) {
                    if (this.getOwner().getMarkMap().containsKey("静水流深")) {
                        long shieldVal = Math.round(0.08F * this.getOwner().getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition());
                        BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                                new BuffDTO(EffectTypeEnum.VAL_SHIELD, "护盾", shieldVal, BuffOverrideStrategyEnum.OVERRIDE),
                                battleEffect.getBattleRound().getBuilder());
                    }
                }

                @Override
                public void ifEnemyHitMe(BattleEffectDTO battleEffect) {
                    if (SKILL_50.equals(battleEffect.getNowSkill())
                            || SKILL_51.equals(battleEffect.getNowSkill())
                            || SKILL_52.equals(battleEffect.getNowSkill())
                            || SKILL_53.equals(battleEffect.getNowSkill())) {
                        this.getOwner().getMarkMap().remove("静水流深");
                        this.getOwner().getMarkMap().put("怒意狂澜", 1);
                        battleEffect.getBattleRound().getBuilder().append("，")
                                .append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                                .append("进入“怒意狂澜”状态");
                    }

                    if (SKILL_51.equals(battleEffect.getNowSkill()) || SKILL_52.equals(battleEffect.getNowSkill())) {
                        Map<String, Integer> fromMarkMap = battleEffect.getFrom().getMarkMap();
                        fromMarkMap.put("薪火", fromMarkMap.getOrDefault("薪火", 0) + 10);
                    }
                }

                @Override
                public void beforeMeDamage(BattleEffectDTO battleEffect) {
                    if (SKILL_50.equals(battleEffect.getNowSkill())
                            || SKILL_51.equals(battleEffect.getNowSkill())
                            || SKILL_52.equals(battleEffect.getNowSkill())
                            || SKILL_53.equals(battleEffect.getNowSkill())) {
                        battleEffect.getDamage().set(2 * battleEffect.getDamage().get());
                    }
                }
            }
    ),

    SKILL_1061(1061L, "镜河揽月待风起",
            "对敌方全体造成600%法术伤害。“静水流深”状态下，三回合内，自身受到伤害时将反弹相当于该次伤害15%的真实伤害。" +
                    "“怒意狂澜”状态下，使敌方全体降低15%攻击与法强持续三回合。",
            Constants.NONE, new SkillEffectDTO(TargetEnum.ALL_ENEMY, DamageTypeEnum.MAGIC, 6F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    if (battleEffect.getFrom().getMarkMap().containsKey("静水流深")) {
                        BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                                new BuffDTO(EffectTypeEnum.REFLECT_DAMAGE, 0.15F), 3,
                                battleEffect.getBattleRound().getBuilder());
                    }
                    if (battleEffect.getFrom().getMarkMap().containsKey("怒意狂澜")) {
                        battleEffect.getEnemy().forEach(b -> {
                            BuffUtil.addBuff(battleEffect.getFrom(), b,
                                    new BuffDTO(EffectTypeEnum.ATK, 0.15F), 3,
                                    battleEffect.getBattleRound().getBuilder());
                            BuffUtil.addBuff(battleEffect.getFrom(), b,
                                    new BuffDTO(EffectTypeEnum.MAGIC_ATK, 0.15F), 3,
                                    battleEffect.getBattleRound().getBuilder());
                        });
                    }
                }
            }
    ),

    SKILL_1062(1062L, "沧渊千里",
            "提升自身25%攻击与法强持续三回合，“静水流深”状态下使敌方全体陷入“眩晕”异常持续一回合。“怒意狂澜”状态下额外对敌方造成400%物理+250%法术伤害",
            Constants.NONE, Arrays.asList(
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 4F,
                    new ThisEffectExtraBattleProcessTemplate() {
                        @Override
                        public void ifHit(BattleEffectDTO battleEffect) {
                            if (battleEffect.getFrom().getMarkMap().containsKey("静水流深")) {
                                battleEffect.getEnemy().forEach(e -> {
                                    AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                                            .from(battleEffect.getFrom()).tar(e)
                                            .round(1).builder(battleEffect.getBattleRound().getBuilder()).build();
                                    AbnormalEnum.VERTIGO.doEffect(input);
                                });
                            }
                        }

                        @Override
                        public void changeDamageRate(BattleEffectDTO battleEffect) {
                            if (battleEffect.getFrom().getMarkMap().containsKey("静水流深")) {
                                battleEffect.setDamageRate(BigDecimal.ZERO);
                            }
                        }

                        @Override
                        public void afterEffect(BattleEffectDTO battleEffect) {
                            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                            BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                                    new BuffDTO(EffectTypeEnum.ATK, 0.25F), 3, builder);
                            BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                                    new BuffDTO(EffectTypeEnum.MAGIC_ATK, 0.25F), 3, builder);
                        }
                    }),
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 2.5F,
                    new ThisEffectExtraBattleProcessTemplate() {
                        @Override
                        public void changeDamageRate(BattleEffectDTO battleEffect) {
                            if (battleEffect.getFrom().getMarkMap().containsKey("静水流深")) {
                                battleEffect.setDamageRate(BigDecimal.ZERO);
                            }
                        }
                    })
    )),

    SKILL_1063(1063L, "潮生潮起归无意",
            "当前处于“静水流深”状态时则切换为“怒意狂澜”状态。当前处于“怒意狂澜”状态时则切换为“静水流深”状态。" +
                    "对敌方全体造成十五次130%物理伤害。每段伤害有20%概率使敌方陷入“溺水”异常持续一回合",
            Constants.NONE, 10000L, Stream.concat(
            Stream.of(new SkillEffectDTO(TargetEnum.ME,
                    new ThisEffectExtraBattleProcessTemplate() {
                        @Override
                        public void ifHit(BattleEffectDTO battleEffect) {
                            Map<String, Integer> markMap = battleEffect.getFrom().getMarkMap();
                            if (markMap.containsKey("静水流深")) {
                                markMap.remove("静水流深");
                                markMap.put("怒意狂澜", 1);
                            } else if (markMap.containsKey("怒意狂澜")) {
                                markMap.remove("怒意狂澜");
                                markMap.put("怒意狂澜", 1);
                            }
                        }
                    })),
            Collections.nCopies(15, new SkillEffectDTO(TargetEnum.ALL_ENEMY, DamageTypeEnum.ATK, 1.3F,
                    new ThisEffectExtraBattleProcessTemplate() {
                        @Override
                        public void ifHit(BattleEffectDTO battleEffect) {
                            if (RandomUtil.isHit(0.2F)) {
                                AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                                        .from(battleEffect.getFrom()).tar(battleEffect.getTar())
                                        .round(1).builder(battleEffect.getBattleRound().getBuilder()).build();
                                AbnormalEnum.DROWNING.doEffect(input);
                            }
                        }
                    }
            )).stream()).collect(Collectors.toList())
    ),

    SKILL_1064(1064L, "冥水数珠", "对敌方连续造成三次90%物理伤害", Constants.NONE,
            Collections.nCopies(3, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 0.9F))
    ),

    SKILL_1065(1065L, "古龙之血·生魂", "血量回复效果提升30%，每回合开始回复自身最大血量的3%", Constants.NONE, false,
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeBattle(BattleFieldDTO battleField) {
                    StringBuilder builder = new StringBuilder("※")
                            .append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的被动【古龙之血·生魂】被触发");
                    RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.HEAL, "古龙之血·生魂", 0.3F, builder);
                    battleField.getBattleMsg().add(builder.toString());
                }

                @Override
                public void beforeMyRound(BattleRoundDTO battleRound) {
                    battleRound.getBuilder().append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的被动【古龙之血·生魂】被触发");
                    long heal = Math.round(0.03F * battleRound.getFrom().getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition());
                    BattleUtil.doHealing(battleRound.getFrom(), heal, battleRound.getBuilder(), battleRound.getBattleField());
                }
            }
    ),

    SKILL_1067(1067L, "卒莫消长（异维）",
            "全部回合开始时触发，当前回合为奇数回合时，提升自身30%法强，降低自身30%防御持续一回合。" +
                    "偶数回合开始时，提升自身30%防御，降低自身30%法强持续一回合", Constants.NONE, false,
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeEachRound(BattleRoundDTO battleRound) {
                    int round = battleRound.getBattleField().getRound();
                    battleRound.getBuilder().append("※")
                            .append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的被动【卒莫消长（异维）】被触发");
                    if (round % 2 == 1) {
                        BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                                new BuffDTO(EffectTypeEnum.MAGIC_ATK, 0.3F), 1, battleRound.getBuilder());
                        BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                                new BuffDTO(EffectTypeEnum.DEF, -0.3F), 1, battleRound.getBuilder());
                    } else {
                        BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                                new BuffDTO(EffectTypeEnum.DEF, 0.3F), 1, battleRound.getBuilder());
                        BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                                new BuffDTO(EffectTypeEnum.MAGIC_ATK, -0.3F), 1, battleRound.getBuilder());
                    }
                }
            }
    ),

    SKILL_1068(1068L, "奇茸借法", "提升自身10%法强持续一回合，造成130%法术伤害",
            Constants.NONE, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 1.3F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getFrom(),
                            new BuffDTO(EffectTypeEnum.MAGIC_ATK, 0.1F), 1,
                            battleEffect.getBattleRound().getBuilder());
                }
            }
    ),

    SKILL_1069(1069L, "明芯指引", "清除敌方全体增益buff效果，命中提升99%持续0回合，对敌方全体造成550%法术伤害",
            Constants.NONE, 80L, new SkillEffectDTO(TargetEnum.ALL_ENEMY, DamageTypeEnum.MAGIC, 5.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void beforeBehavior(BattleRoundDTO battleRound) {
                    BuffUtil.addBuff(battleRound.getFrom(), battleRound.getFrom(),
                            new BuffDTO(EffectTypeEnum.HIT, 0.99F), 0, battleRound.getBuilder());
                }
            }
    ),

    SKILL_1070(1070L, "芳灵流碎", "扣除自身20%最大血量，使敌方陷入眩晕异常持续两回合",
            Constants.NONE, new SkillEffectDTO(TargetEnum.ANY_ENEMY),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void afterEffect(BattleEffectDTO battleEffect) {
                    BattleDTO from = battleEffect.getFrom();
                    BattleDTO tar = battleEffect.getTar();
                    Long maxHp = BattleUtil.getLongProperty(
                            from.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition(),
                            OrganismPropertiesEnum.MAX_HP.getFieldName(), from,
                            battleEffect.getBattleRound().getBattleField());
                    BattleEffectDTO effect = BattleEffectDTO.builder()
                            .damage(new AtomicLong(Math.round(0.2 * maxHp)))
                            .battleRound(battleEffect.getBattleRound())
                            .from(from).tar(from).build();
                    BattleUtil.doDamage(effect);
                    AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                            .from(from).tar(tar).round(2).builder(battleEffect.getBattleRound().getBuilder()).build();
                    AbnormalEnum.VERTIGO.doEffect(input);
                }
            }
    ),

    SKILL_1071(1071L, "灵莲悄恸·妖神卍解",
            "整场战斗限用一次，死亡时触发，回复自身全部血量与蓝量。被妖神M3吞噬，最大血量最大蓝量提升至200%，法强防御命中闪避速度提升50%持续整场战斗。" +
                    "\n妖神绝语∶每回合回复5000点血量",
            Constants.NONE, false, new ExtraBattleProcessTemplate() {

        @Override
        public void beforeMyRound(BattleRoundDTO battleRound) {
            StringBuilder builder = battleRound.getBuilder();
            builder.append("※").append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                    .append("的被动技能【妖神绝语】被触发");
            BattleUtil.doHealing(this.getOwner(), 5000, builder, battleRound.getBattleField());
        }

        @Override
        public void beforeMeDeath(BattleEffectDTO battleEffect) {
            // 因为是受到致命伤害，此处的from应为造成伤害者，tar才是受到致命伤害者
            BattleDTO tar = battleEffect.getTar();
            if (tar.getMarkMap().containsKey("已触发灵莲悄恸·妖神卍解")) {
                return;
            }
            tar.getMarkMap().put("已触发灵莲悄恸·妖神卍解", 1);
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            battleEffect.getDamage().set(0L);
            builder.append("，☠").append(tar.getOrganismInfoDTO().getOrganismDTO().getName()).append("受到致命伤害，被妖神M3吞噬");
            long maxHp = BattleUtil.getLongProperty(
                    tar.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition(),
                    OrganismPropertiesEnum.MAX_HP.getFieldName(), tar,
                    battleEffect.getBattleRound().getBattleField());
            long maxMp = BattleUtil.getLongProperty(
                    tar.getOrganismInfoDTO().getOrganismDTO().getMaxMpWithAddition(),
                    OrganismPropertiesEnum.MAX_MP.getFieldName(), tar,
                    battleEffect.getBattleRound().getBattleField());
            tar.getOrganismInfoDTO().getOrganismDTO().setMaxHpWithAddition(2 * maxHp);
            tar.getOrganismInfoDTO().getOrganismDTO().setMaxMpWithAddition(2 * maxMp);
            BattleUtil.doHealing(tar, 2 * maxHp, builder, battleEffect.getBattleRound().getBattleField());
            BattleUtil.doMpChange(tar, 2 * maxMp, builder);
            BuffUtil.addBuff(tar, tar, new BuffDTO(EffectTypeEnum.MAGIC_ATK, 0.5F), builder);
            BuffUtil.addBuff(tar, tar, new BuffDTO(EffectTypeEnum.DEF, 0.5F), builder);
            BuffUtil.addBuff(tar, tar, new BuffDTO(EffectTypeEnum.HIT, 0.5F), builder);
            BuffUtil.addBuff(tar, tar, new BuffDTO(EffectTypeEnum.DODGE, 0.5F), builder);
            BuffUtil.addBuff(tar, tar, new BuffDTO(EffectTypeEnum.SPEED, 0.5F), builder);
        }
    }),

    SKILL_1072(1072L, "异维入侵", "M3·米莉儿天生免疫全部异常效果", Constants.NONE, false,
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeBattle(BattleFieldDTO battleField) {
                    StringBuilder builder = new StringBuilder("※")
                            .append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                            .append("的被动【异维入侵】被触发");
                    RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.IMMUNITY, "异维入侵", 0L, builder);
                }
            }
    ),

    SKILL_1073(1073L, "天犼幻金瞳", "自身每行动三次进行一次“洞察”预知敌方的行动，提高自身50%闪避持续一回合，" +
            "洞察期间每成功闪避一次攻击则提升自身50%攻击持续一回合，最多不超过150%，同时提升自身15%双防持续两回合",
            Constants.NONE, false, new ExtraBattleProcessTemplate() {
        @Override
        public void afterMyBehavior(BattleEffectDTO battleEffect) {
            int cnt = this.getOwner().getMarkMap().getOrDefault("洞察", 0);
            cnt++;
            if (cnt >= 3) {
                cnt = 0;
                StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                builder.append("，").append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName()).append("进入“洞察”状态");
                BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.DODGE, "洞察", 0.5F), 1, builder);
            }
            this.getOwner().getMarkMap().put("洞察", cnt);
        }

        @Override
        public void ifEnemyNotHitMe(BattleEffectDTO battleEffect) {
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            builder.append("，").append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName()).append("洞若观火");
            BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.DEF, 0.15F), 2, builder);
            BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.MAGIC_DEF, 0.15F), 2, builder);

            List<BattleBuffDTO> buffList = this.getOwner().getBuffMap().getOrDefault(EffectTypeEnum.DODGE, new ArrayList<>());
            long cnt = buffList.stream().filter(buff -> "洞察".equals(buff.getBuffDTO().getBuffName())).count();
            if (cnt < 3) {
                BuffUtil.addBuff(this.getOwner(), this.getOwner(), new BuffDTO(EffectTypeEnum.ATK, 0.5F), 1, builder);
            }
        }
    }),

    SKILL_1074(1074L, "眩瞳魔光术", "金眼魔童释放金瞳魔光，敌方全体命中降低25%持续两回合，对敌方造成450%物理伤害",
            Constants.NONE, 90L, new SkillEffectDTO(TargetEnum.ALL_ENEMY, DamageTypeEnum.ATK, 4.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    BuffUtil.addBuff(battleEffect.getFrom(), battleEffect.getTar(),
                            new BuffDTO(EffectTypeEnum.HIT, -0.25F), 2,
                            battleEffect.getBattleRound().getBuilder());
                }
            }
    ),

    SKILL_1075(1075L, "雷荒", "全部攻击附加15%概率麻痹异常", Constants.NONE, false,
            new ExtraBattleProcessTemplate() {
                @Override
                public void ifMeHitEnemy(BattleEffectDTO battleEffect) {
                    if (RandomUtil.isHit(0.15F)) {
                        AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                                .from(battleEffect.getFrom()).tar(battleEffect.getTar()).round(1)
                                .builder(battleEffect.getBattleRound().getBuilder()).build();
                        AbnormalEnum.NUMB.doEffect(input);
                    }
                }
            }
    ),

    SKILL_1076(1076L, "三玄道雷", "雷劫的前三次攻击，每一次都更加凌厉，三次达到最高值。每次攻击永久增加自身15%最终伤害",
            Constants.NONE, false, new ExtraBattleProcessTemplate() {
        @Override
        public void afterMyBehavior(BattleEffectDTO battleEffect) {
            int cnt = this.getOwner().getMarkMap().getOrDefault("三玄道雷", 0);
            if (cnt < 3) {
                cnt++;
                this.getOwner().getMarkMap().put("三玄道雷", cnt);
                StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                builder.append("，").append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                        .append("的被动【三玄道雷】被触发");
                BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                        new BuffDTO(EffectTypeEnum.DO_DAMAGE, 0.15F), builder);
            }
        }
    }),

    SKILL_1077(1077L, "雷罚", "造成250%物理伤害", Constants.NONE,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 2.5F)
    ),

    SKILL_1078(1078L, "狂雷天瀑", "对敌方全体造成550%物理伤害", Constants.NONE, 100L,
            new SkillEffectDTO(TargetEnum.ALL_ENEMY, DamageTypeEnum.ATK, 5.5F)
    ),

    SKILL_1079(1079L, "八卦囚龙阵", "展开囚龙阵法，敌方速度与闪避降低30%持续三回合，使敌方眩晕持续一回合，"
            + "阵法展开三回合内每回合使敌方损失5%最大血量（最多不超过100000）和蓝量", Constants.NONE, 120L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void afterEffect(BattleEffectDTO battleEffect) {
            BattleDTO from = battleEffect.getFrom();
            BattleDTO tar = battleEffect.getTar();
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            BuffUtil.addBuff(from, tar, new BuffDTO(EffectTypeEnum.SPEED, -0.3F), 3, builder);
            BuffUtil.addBuff(from, tar, new BuffDTO(EffectTypeEnum.DODGE, -0.3F), 3, builder);
            BuffUtil.addBuff(from, tar, new BuffDTO(EffectTypeEnum.BLEEDING, -0.05F), 3, builder);
            BuffUtil.addBuff(from, tar, new BuffDTO(EffectTypeEnum.PAUSE, "眩晕", true), 1, builder);
        }
    }),

    SKILL_1080(1080L, "星王·死告术", "场地效果改为无疆死域", Constants.NONE, false,
            new ExtraBattleProcessTemplate() {
                @Override
                public void beforeBattle(BattleFieldDTO battleField) {
                    battleField.changeFieldEffect(FieldEffectEnum.EFFECT_4);
                }
            }
    ),

    SKILL_1081(1081L, "星王·炼魂术", "星神·枯冢之骸于无疆死域中每获得一枚“死魂”将进行炼化，" +
            "每累计获得十枚“死魂”后将召唤亡灵，随后清除全部“死魂”",
            Constants.NONE, false, new ExtraBattleProcessTemplate() {
        @Override
        public void specialExecute(Object obj) {
            BattleRoundDTO battleRound = (BattleRoundDTO) obj;
            Map<String, Integer> markMap = battleRound.getFrom().getMarkMap();
            int cnt = markMap.getOrDefault("死魂", 0);
            if (cnt >= 0) {
                markMap.put("死魂", 0);
                addUndead(battleRound);
            }
        }

        private void addUndead(BattleRoundDTO battleRound) {
            SkillBarDTO skillBar = new SkillBarDTO();
            skillBar.setSkillId(SkillEnum.SKILL_11.getId());
            skillBar.setHead(skillBar);
            OrganismDTO undeadOrganism = SummonTemplateEnum.UNDEAD
                    .constructOrganism(battleRound.getFrom().getOrganismInfoDTO().getOrganismDTO());
            BattleUtil.addBattleDTO(battleRound.getFrom().getOrganismInfoDTO().getOrganismDTO(), undeadOrganism, skillBar,
                    1, battleRound.getOur(), 0, battleRound.getBuilder());
        }
    }),

    SKILL_1082(1082L, "鬼恸呼嚎", "对敌方全体造成550%法术伤害，星神当前存在五枚以上“死魂”时，额外使敌方陷入“失神”异常",
            Constants.NONE, 80L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 5.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    if (battleEffect.getFrom().getMarkMap().getOrDefault("死魂", 0) >= 5) {
                        AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                                .from(battleEffect.getFrom()).tar(battleEffect.getTar()).round(1)
                                .builder(battleEffect.getBattleRound().getBuilder()).build();
                        AbnormalEnum.ABSENT_MINDED.doEffect(input);
                    }
                }
            }
    ),

    SKILL_1083(1083L, "星王·爆灵术", "对敌方造成350%+200%*“生魂”数量点法术伤害，“生魂”计数最多为10，释放技能后清除全部“生魂”",
            Constants.NONE, 180L, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 3.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void changeDamageRate(BattleEffectDTO battleEffect) {
                    int cnt = battleEffect.getFrom().getMarkMap().getOrDefault("生魂", 0);
                    cnt = Math.min(cnt, 10);
                    battleEffect.setDamageRate(battleEffect.getDamageRate().add(BigDecimal.valueOf(2 * cnt)));
                    battleEffect.getFrom().getMarkMap().put("生魂", 0);
                }
            }
    ),

    SKILL_1084(1084L, "星王·墓骨死士", "立即于身前召唤两只“亡灵”，全体亡灵全属性提升30%持续三回合", Constants.NONE, 120L,
            new SkillEffectDTO(TargetEnum.ME), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void ifHit(BattleEffectDTO battleEffect) {
            addUndead(battleEffect.getBattleRound(), 2);
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            battleEffect.getOur().stream()
                    .filter(BattleDTO::getSummoned)
                    .filter(b -> "亡灵".equals(b.getOrganismInfoDTO().getOrganismDTO().getName()))
                    .filter(b -> b.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0L)
                    .forEach(b -> {
                        BuffUtil.addBuff(battleEffect.getFrom(), b, new BuffDTO(EffectTypeEnum.ATK, 0.3F), 1, builder);
                        BuffUtil.addBuff(battleEffect.getFrom(), b, new BuffDTO(EffectTypeEnum.MAGIC_ATK, 0.3F), 1, builder);
                        BuffUtil.addBuff(battleEffect.getFrom(), b, new BuffDTO(EffectTypeEnum.DEF, 0.3F), 1, builder);
                        BuffUtil.addBuff(battleEffect.getFrom(), b, new BuffDTO(EffectTypeEnum.MAGIC_DEF, 0.3F), 1, builder);
                        BuffUtil.addBuff(battleEffect.getFrom(), b, new BuffDTO(EffectTypeEnum.SPEED, 0.3F), 1, builder);
                        BuffUtil.addBuff(battleEffect.getFrom(), b, new BuffDTO(EffectTypeEnum.HIT, 0.3F), 1, builder);
                        BuffUtil.addBuff(battleEffect.getFrom(), b, new BuffDTO(EffectTypeEnum.DODGE, 0.3F), 1, builder);
                    });
        }

        private void addUndead(BattleRoundDTO battleRound, int count) {
            SkillBarDTO skillBar = new SkillBarDTO();
            skillBar.setSkillId(SkillEnum.SKILL_11.getId());
            skillBar.setHead(skillBar);
            OrganismDTO undeadOrganism = SummonTemplateEnum.UNDEAD
                    .constructOrganism(battleRound.getFrom().getOrganismInfoDTO().getOrganismDTO());
            BattleUtil.addBattleDTO(battleRound.getFrom().getOrganismInfoDTO().getOrganismDTO(), undeadOrganism, skillBar,
                    count, battleRound.getOur(), 0, battleRound.getBuilder());
        }
    }),

    SKILL_1085(1085L, "寰宇皆知",
            "获得“博识”状态，攻击命中时有70%概率识破敌人弱点使该次伤害提升至150%，受到攻击时有70%概率看破敌人的攻击，使该次伤害降低至80%",
            Constants.NONE, false, new ExtraBattleProcessTemplate() {
        @Override
        public void ifMeHitEnemy(BattleEffectDTO battleEffect) {
            if (RandomUtil.isHit(0.7F)) {
                StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                builder.append("，").append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                        .append("识破弱点，伤害提升至150%");
                battleEffect.getDamage().set(Math.round(battleEffect.getDamage().get() * 1.5F));
                if (SKILL_1086.equals(battleEffect.getNowSkill()) || SKILL_1087.equals(battleEffect.getNowSkill())) {
                    ExtraBattleProcessTemplate specialExecuteTpl = battleEffect.getBattleRound().getBattleField()
                            .getExtraBattleProcessTemplateList().stream()
                            .filter(extra -> battleEffect.getFrom().equals(extra.getOwner()))
                            .filter(extra -> extra.getClass().equals(battleEffect.getNowSkill().getGlobalExtraProcess().getClass()))
                            .findFirst().orElse(null);
                    if (Objects.nonNull(specialExecuteTpl)) {
                        specialExecuteTpl.executeSpecialExecute(battleEffect);
                    }
                }
                if (SKILL_1089.equals(battleEffect.getNowSkill())) {
                    long damage = BattleUtil.getDamage(this.getOwner(), battleEffect.getTar(), battleEffect.getBattleRound(),
                            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 17F));
                    BattleUtil.doDamage(this.getOwner(), battleEffect.getTar(), DamageTypeEnum.ATK, damage, battleEffect.getBattleRound());
                }
                if (this.getOwner().getMarkMap().containsKey("众法守相")) {
                    BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                            new BuffDTO(EffectTypeEnum.ATK, "众法守相", 0.25F, BuffOverrideStrategyEnum.ROUND_PLUS),
                            3, builder);
                    BuffUtil.addBuff(this.getOwner(), this.getOwner(),
                            new BuffDTO(EffectTypeEnum.MAGIC_ATK, "众法守相", 0.25F, BuffOverrideStrategyEnum.ROUND_PLUS),
                            3, builder);
                }
            }
        }

        @Override
        public void beforeMeDamage(BattleEffectDTO battleEffect) {
            if (RandomUtil.isHit(0.7F)) {
                battleEffect.getBattleRound().getBuilder().append("，")
                        .append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName())
                        .append("看破敌人的攻击，伤害降低至80%");
                battleEffect.getDamage().set(Math.round(battleEffect.getDamage().get() * 0.8F));
            }
        }
    }),

    SKILL_1086(1086L, "以理服人", "造成750%物理伤害，若命中敌人“弱点”则使敌方眩晕一回合", Constants.NONE, 100L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 7.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
            },
            new ExtraBattleProcessTemplate() {
                @Override
                public void specialExecute(Object obj) {
                    BattleEffectDTO battleEffect = (BattleEffectDTO) obj;
                    AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                            .from(battleEffect.getFrom()).tar(battleEffect.getTar())
                            .builder(battleEffect.getBattleRound().getBuilder()).round(1).build();
                    AbnormalEnum.VERTIGO.doEffect(input);
                }
            }
    ),

    SKILL_1087(1087L, "百法缭乱", "造成750%法术伤害，若命中敌人弱点则使敌人陷入“焚毁”与“溺水”持续一回合", Constants.NONE, 100L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 7.5F),
            new ThisBehaviorExtraBattleProcessTemplate() {
            },
            new ExtraBattleProcessTemplate() {
                @Override
                public void specialExecute(Object obj) {
                    BattleEffectDTO battleEffect = (BattleEffectDTO) obj;
                    AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                            .from(battleEffect.getFrom()).tar(battleEffect.getTar())
                            .builder(battleEffect.getBattleRound().getBuilder()).round(1).build();
                    AbnormalEnum.BURN_DOWN.doEffect(input);
                    AbnormalEnum.DROWNING.doEffect(input);
                }
            }
    ),

    SKILL_1088(1088L, "众法守相", "提高自身25%攻击 25%法强 25%防御 25%法抗持续三回合，"
            + "三回合内若看破敌人攻击则提高自身50%攻击 50%法强持续一回合。装备该技能时每回合回复自身10蓝量", Constants.NONE, 100L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void ifHit(BattleEffectDTO battleEffect) {
            BattleDTO from = battleEffect.getFrom();
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.ATK, 0.25F), 3, builder);
            BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.MAGIC_ATK, 0.25F), 3, builder);
            BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.DEF, 0.25F), 3, builder);
            BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.MAGIC_DEF, 0.25F), 3, builder);
            from.getMarkMap().put("众法守相", 3);
        }
    }, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMyRound(BattleRoundDTO battleRound) {
            BattleDTO owner = this.getOwner();
            if (owner.getMarkMap().containsKey("众法守相")) {
                int round = owner.getMarkMap().get("众法守相") - 1;
                if (round <= 1) {
                    owner.getMarkMap().remove("众法守相");
                } else {
                    owner.getMarkMap().put("众法守相", round);
                }
            }
        }
    }),

    SKILL_1089(1089L, "万法归一", "造成1700%法术伤害，若识破敌人弱点，则额外造成1700%物理伤害", Constants.NONE, 100L,
            new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.MAGIC, 17F)
    ),

    SKILL_1090(1090L, "如血与啮噬之海", "开局，血魔为敌方全体种入“血之花”，自身获得100层“血之华”。血魔每有一层“血之华”使自身受到的伤害减少1%。" +
            "敌方每次攻击命中血魔后，血魔减少一层“血之华”，对应敌方“血之花”获得一点成熟度。\n" +
            "“血之花”按照对应成熟度成长为对应等级并获得对应效果。\n" +
            "一级∶5-10 血之花持有者提高100%攻击，100%法强\n" +
            "二级∶11–30 血之花持有者提高200%攻击 200%法强 造成伤害提高100% 每回合流失最大血量的1%。\n" +
            "三级：31–50 血之花持有者提高300%攻击 300%法强 造成伤害提高150%。双抗降低50% 每回合流失最大血量的5%。\n" +
            "四级：50-59 血之花持有者降低80%攻击 80%法强，双抗降低50%，每回合流失最大血量8%。\n" +
            "五级：60- 血之花完全绽放，对血之花持有者造成999999999点真实伤害。\n" +
            "注∶“血之花”按照对应等级触发对应效果，不同时触发其他等级效果。",
            Constants.NONE, false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeBattle(BattleFieldDTO battleField) {
            BattleDTO owner = this.getOwner();
            owner.getMarkMap().put("血之华", 100);
            battleField.getBattleMsg().add("※" + owner.getOrganismInfoDTO().getOrganismDTO().getName()
                    + "的被动【如血与啮噬之海】被触发，获得100层“血之华”");
        }

        @Override
        public void beforeEnemyBehavior(BattleEffectDTO battleEffect) {
            if (SKILL_55.equals(battleEffect.getNowSkill()) || SKILL_59.equals(battleEffect.getNowSkill())) {
                battleEffect.setDamageRate(battleEffect.getDamageRate().add(BigDecimal.TEN));
            }
        }

        @Override
        public void beforeMeDamage(BattleEffectDTO battleEffect) {
            if (SKILL_58.equals(battleEffect.getNowSkill())) {
                battleEffect.getDamage().set(2 * battleEffect.getDamage().get());
            }
        }

        @Override
        public void ifEnemyHitMe(BattleEffectDTO battleEffect) {
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            if (battleEffect.getDamageRate().floatValue() <= 0) {
                return;
            }
            if (SKILL_59.equals(battleEffect.getNowSkill())) {
                BuffUtil.addBuff(battleEffect.getFrom(), this.getOwner(), new BuffDTO(EffectTypeEnum.DEF, -0.15F), 3, builder);
            }
            BattleDTO from = battleEffect.getFrom();
            Map<String, Integer> fromMarkMap = from.getMarkMap();
            int round = fromMarkMap.getOrDefault("触发血之花回合", 0);
            if (round == battleEffect.getBattleRound().getBattleField().getRound()) {
                return;
            }
            int changedQty = this.getOwner().getMarkMap().getOrDefault("血之华姿态", 1) == 2
                    && (SKILL_58.equals(battleEffect.getNowSkill()) || SKILL_59.equals(battleEffect.getNowSkill())) ? -1 : -3;
            int realChangedQty = changedBloodCnt(this.getOwner(), changedQty, battleEffect);
            fromMarkMap.put("触发血之花回合", battleEffect.getBattleRound().getBattleField().getRound());
            changedBloodFlower(from, -realChangedQty, builder);
        }
    }),

    SKILL_1091(1091L, "如恶与苦难之灾",
            "血魔失去全部“血之华”后展现第二姿态，双抗降低50%，双攻提高50%，每回合收回敌方全体“血之花”一点成熟度并获得对应数量的“血之华”",
            Constants.NONE, false, new ExtraBattleProcessTemplate() {
        @Override
        public void beforeMyRound(BattleRoundDTO battleRound) {
            Map<String, Integer> markMap = this.getOwner().getMarkMap();
            StringBuilder builder = battleRound.getBuilder();
            if (markMap.getOrDefault("血之华", 0) == 0 && markMap.getOrDefault("血之华姿态", 1) == 1) {
                markMap.put("血之华姿态", 2);
                builder.append("，").append(this.getOwner().getOrganismInfoDTO().getOrganismDTO().getName()).append("展现第二姿态");
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.DEF, "血之华第二姿态", -0.5F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.MAGIC_DEF, "血之华第二姿态", -0.5F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.ATK, "血之华第二姿态", 0.5F, builder);
                RuleUtil.addRule(this.getOwner(), this.getOwner(), EffectTypeEnum.MAGIC_ATK, "血之华第二姿态", 0.5F, builder);
            }
            if (markMap.getOrDefault("血之华姿态", 1) == 2) {
                battleRound.getEnemy().forEach(e -> changedBloodFlower(e, -1, builder));
            }
        }
    }),

    SKILL_1092(1092L, "如命与腐世之怠", "对敌方造成800%+300%x敌方血之花等级点物理伤害。有（10%+10%x敌方血之花等级）概率使敌方眩晕一回合",
            Constants.NONE, new SkillEffectDTO(TargetEnum.ANY_ENEMY, DamageTypeEnum.ATK, 8F),
            new ThisBehaviorExtraBattleProcessTemplate() {
                @Override
                public void changeDamageRate(BattleEffectDTO battleEffect) {
                    int bloodFlower = battleEffect.getTar().getMarkMap().getOrDefault("血之花", 0);
                    battleEffect.setDamageRate(battleEffect.getDamageRate()
                            .add(BigDecimal.valueOf(0.3F).multiply(BigDecimal.valueOf(bloodFlower))));
                }

                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    float rate = 0.1F + 0.1F * battleEffect.getTar().getMarkMap().getOrDefault("血之花", 0);
                    if (RandomUtil.isHit(rate)) {
                        AbnormalEnum.AbnormalInput input = AbnormalEnum.AbnormalInput.builder()
                                .from(battleEffect.getFrom()).tar(battleEffect.getTar()).round(1)
                                .builder(battleEffect.getBattleRound().getBuilder()).build();
                        AbnormalEnum.VERTIGO.doEffect(input);
                    }
                }
            }
    ),

    SKILL_1093(1093L, "如雨与蔽听之霾",
            "血魔提高自身30%速度30%攻击，降低敌方30%命中。持续五回合。\n第二姿态时，血魔额外收回敌方全体两点“血之花”成熟度",
            Constants.NONE, new SkillEffectDTO(TargetEnum.ME), new ThisBehaviorExtraBattleProcessTemplate() {
        @Override
        public void ifHit(BattleEffectDTO battleEffect) {
            BattleDTO from = battleEffect.getFrom();
            BattleDTO tar = battleEffect.getTar();
            StringBuilder builder = battleEffect.getBattleRound().getBuilder();
            BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.SPEED, 0.3F), 5, builder);
            BuffUtil.addBuff(from, from, new BuffDTO(EffectTypeEnum.ATK, 0.3F), 5, builder);
            BuffUtil.addBuff(from, tar, new BuffDTO(EffectTypeEnum.HIT, -0.3F), 5, builder);
            if (battleEffect.getFrom().getMarkMap().getOrDefault("血之华姿态", 1) == 2) {
                battleEffect.getEnemy().forEach(e -> changedBloodFlower(e, -2, builder));
            }
        }
    }),

    SKILL_1094(1094L, "如罪与生死之爱",
            "第一姿态时对敌方全体造成800%物理伤害，并强制提高敌方全体“血之花”五点成熟度（不消耗血之华）。\n" +
                    "第二姿态时对敌方全体造成800%法术伤害。收回敌方全体“血之花”五点成熟度（同时转化为“血之华”）。", Constants.NONE, Arrays.asList(
            new SkillEffectDTO(TargetEnum.ALL_ENEMY, DamageTypeEnum.ATK, 8F, new ThisEffectExtraBattleProcessTemplate() {
                @Override
                public void changeDamageRate(BattleEffectDTO battleEffect) {
                    if (battleEffect.getFrom().getMarkMap().getOrDefault("血之华姿态", 1) == 2) {
                        battleEffect.setDamageRate(BigDecimal.ZERO);
                    }
                }

                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    if (battleEffect.getFrom().getMarkMap().getOrDefault("血之华姿态", 1) == 1) {
                        battleEffect.getEnemy().forEach(e -> changedBloodFlower(e, 5, battleEffect.getBattleRound().getBuilder()));
                    }
                }
            }),
            new SkillEffectDTO(TargetEnum.ALL_ENEMY, DamageTypeEnum.MAGIC, 8F, new ThisEffectExtraBattleProcessTemplate() {
                @Override
                public void changeDamageRate(BattleEffectDTO battleEffect) {
                    if (battleEffect.getFrom().getMarkMap().getOrDefault("血之华姿态", 1) == 1) {
                        battleEffect.setDamageRate(BigDecimal.ZERO);
                    }
                }

                @Override
                public void ifHit(BattleEffectDTO battleEffect) {
                    if (battleEffect.getFrom().getMarkMap().getOrDefault("血之华姿态", 1) == 2) {
                        battleEffect.getEnemy().forEach(e -> changedBloodFlower(e, -5, battleEffect.getBattleRound().getBuilder()));
                        changedBloodCnt(battleEffect.getFrom(), 5, battleEffect);
                    }
                }
            })
    )),

    /*
    被动：偃相归一——太初法则
开局在队伍最前位召唤一只偃术傀偶，继承自身80%基础属性值。
偃术傀儡
被动 劫承固命：偃术傀儡每次行动前嘲讽敌方全体持续一回合。偃术傀偶每次攻击命中后会为自身附加一层“命”，每层“命”使偃术傀偶的双防提升10%，最多可叠加八层“命”。
普攻 普通攻击
技能1 命转相合∶ 消耗一层“命”，令友方全体获得相当于自身最大血量12%的护盾。蓝耗0
技能2 太初鸣誓——归元守一：消耗半数“命”，每消耗一层命使友方全体获得8%免伤，使敌方全体受到的物理与法术伤害提高10%。蓝耗0
     */;
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
//        DEFAULT_SKILL_MAP.put(JobEnum.CHEATER.getJobCode(), 6L);
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

    public String getName(BattleRoundDTO battleRound) {
        return getName();
    }

    public static int changedBloodCnt(BattleDTO battleDTO, int changedQty, BattleEffectDTO battleEffect) {
        Map<String, Integer> ownerMarkMap = battleDTO.getMarkMap();
        int bloodCnt = ownerMarkMap.getOrDefault("血之华", 0);
        StringBuilder builder = battleEffect.getBattleRound().getBuilder();
        builder.append("，").append(battleDTO.getOrganismInfoDTO().getOrganismDTO().getName()).append("“血之华”层数");
        int newBloodCnt = bloodCnt + changedQty;
        newBloodCnt = Math.max(0, newBloodCnt);
        int realChangedQty = newBloodCnt - bloodCnt;
        if (realChangedQty >= 0) {
            builder.append("+");
        }
        builder.append(realChangedQty).append("，当前：").append(newBloodCnt);
        if (newBloodCnt > 0) {
            float damageRate = 0.01F * newBloodCnt;
            builder.append("，受到伤害降低").append(damageRate * 100).append("%");
            battleEffect.getDamage().set(Math.round(battleEffect.getDamage().get() * (1F - damageRate)));
        }
        ownerMarkMap.put("血之华", newBloodCnt);
        return realChangedQty;
    }

    public static void changedBloodFlower(BattleDTO battleDTO, int changedQty, StringBuilder builder) {
        Map<String, Integer> markMap = battleDTO.getMarkMap();
        int oldBloodFlower = markMap.getOrDefault("血之花", 0);
        int newBloodFlower = oldBloodFlower + changedQty;
        newBloodFlower = Math.max(0, newBloodFlower);

        markMap.put("血之花", newBloodFlower);
        builder.append("，").append(battleDTO.getOrganismInfoDTO().getOrganismDTO().getName()).append("“血之花”成熟度");
        if (changedQty >= 0) {
            builder.append("+");
        }
        builder.append(changedQty).append("，当前：").append(newBloodFlower);

        RuleUtil.removeRule(battleDTO, "血之花");
        if (newBloodFlower >= 5 && newBloodFlower <= 10) {
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.ATK, "血之花", 1F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.MAGIC_ATK, "血之花", 1F, builder);
        } else if (newBloodFlower >= 11 && newBloodFlower <= 30) {
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.ATK, "血之花", 2F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.MAGIC_ATK, "血之花", 2F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.DO_DAMAGE, "血之花", 1F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.BLEEDING, "血之花", 0.01F, builder);
        } else if (newBloodFlower >= 31 && newBloodFlower <= 50) {
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.ATK, "血之花", 3F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.MAGIC_ATK, "血之花", 3F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.DO_DAMAGE, "血之花", 1.5F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.DEF, "血之花", -0.5F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.MAGIC_DEF, "血之花", -0.5F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.BLEEDING, "血之花", 0.05F, builder);
        } else if (newBloodFlower >= 51 && newBloodFlower <= 59) {
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.ATK, "血之花", -0.8F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.MAGIC_ATK, "血之花", -0.8F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.DEF, "血之花", -0.5F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.MAGIC_DEF, "血之花", -0.5F, builder);
            RuleUtil.addRule(battleDTO, battleDTO, EffectTypeEnum.BLEEDING, "血之花", 0.08F, builder);
        } else if (newBloodFlower >= 60) {
            BattleUtil.doRealDamage(battleDTO, 999999999L, builder);
        }
    }
}
