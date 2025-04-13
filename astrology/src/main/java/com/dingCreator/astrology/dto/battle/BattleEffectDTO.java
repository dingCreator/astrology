package com.dingCreator.astrology.dto.battle;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.dto.organism.OrganismDTO;
import com.dingCreator.astrology.dto.skill.SkillEffectDTO;
import com.dingCreator.astrology.enums.BuffTypeEnum;
import com.dingCreator.astrology.enums.FieldEffectEnum;
import com.dingCreator.astrology.enums.skill.DamageTypeEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.util.BattleUtil;
import com.dingCreator.astrology.util.BuffUtil;
import com.dingCreator.astrology.util.NumberUtil;
import com.dingCreator.astrology.util.RandomUtil;
import com.dingCreator.astrology.util.template.ExtraBattleProcessTemplate;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ding
 * @date 2025/4/10
 */
@Data
@Builder
public class BattleEffectDTO {
    /**
     * 来源
     */
    private BattleDTO from;
    /**
     * 目标
     */
    private BattleDTO tar;
    /**
     * 来源方友方
     */
    private List<BattleDTO> our;
    /**
     * 来源方敌方
     */
    private List<BattleDTO> enemy;
    /**
     * 使用的技能
     */
    private SkillEnum nowSkill;
    /**
     * 技能效果
     */
    private SkillEffectDTO skillEffect;
    /**
     * 伤害量
     */
    private AtomicLong damage;
    /**
     * 是否暴击
     */
    private Boolean critical;
    /**
     * 伤害倍率
     */
    private BigDecimal damageRate;
    /**
     * 所属的轮次
     */
    private BattleRoundDTO battleRound;

    public BattleEffectDTO(BattleDTO from, BattleDTO tar, List<BattleDTO> our, List<BattleDTO> enemy,
                           SkillEnum nowSkill, SkillEffectDTO skillEffect, BattleRoundDTO battleRound) {
        this.from = from;
        this.tar = tar;
        this.our = our;
        this.enemy = enemy;
        this.nowSkill = nowSkill;
        this.skillEffect = skillEffect;
        this.battleRound = battleRound;
    }

    /**
     * 单个目标效果
     */
    public void executeSingleTarBehavior() {
        BattleFieldDTO battleField = battleRound.getBattleField();
        // 插入结算
        List<ExtraBattleProcessTemplate> extraBattleProcessList = battleField.getExtraBattleProcessTemplateList();
        // 插入结算-我的行动前
        extraBattleProcessList.stream().filter(ext -> ext.getFrom().equals(from))
                .forEach(ext -> ext.beforeMyBehavior(this));
        nowSkill.getThisBehaviorExtraProcess().beforeEffect(this);
        // 行动过程
        executeBehavior();
        // 插入结算-我的行动后
        nowSkill.getThisBehaviorExtraProcess().afterEffect(this);
        extraBattleProcessList.stream().filter(ext -> ext.getFrom().equals(from))
                .forEach(ext -> ext.afterMyBehavior(this));
    }

    public void executeBehavior() {
        OrganismDTO fromOrganism = from.getOrganismInfoDTO().getOrganismDTO();
        OrganismDTO tarOrganism = tar.getOrganismInfoDTO().getOrganismDTO();
        BattleFieldDTO battleField = battleRound.getBattleField();
        StringBuilder builder = battleRound.getBuilder();
        // 插入结算
        List<ExtraBattleProcessTemplate> extraBattleProcessList = battleField.getExtraBattleProcessTemplateList();
        // 技能目标为敌方，计算是否命中
        if (skillEffect.getSkillTargetEnum().isEnemy()) {
            if (!BattleUtil.isHit(from, tar, battleField)) {
                // 未命中插入结算
                extraBattleProcessList.forEach(ext -> ext.ifNotHit(this));
                if (Objects.nonNull(nowSkill)) {
                    nowSkill.getThisBehaviorExtraProcess().ifNotHit(this);
                }
                skillEffect.getTemplate().ifNotHit(this);
                battleRound.getBuilder().append(",没有命中").append(tarOrganism.getName());
                return;
            }
        }
        long damage = BattleUtil.getDamage(this);
        damage = Math.round(damage * RandomUtil.rangeFloatRandom(0.9F, 1.1F));
        this.damage = new AtomicLong(damage);
        // 计算暴击
        this.critical = BattleUtil.getCriticalDamage(this);
        // 计算减伤
        if (DamageTypeEnum.ATK.equals(skillEffect.getDamageTypeEnum()) && tar.getBuffMap().containsKey(BuffTypeEnum.DAMAGE)) {
            float reduction = BuffUtil.getRate(0F, BuffTypeEnum.DAMAGE, from);
            this.damage.set(Math.round(this.damage.get() * (1 - reduction)));
        } else if (DamageTypeEnum.MAGIC.equals(skillEffect.getDamageTypeEnum())
                && tar.getBuffMap().containsKey(BuffTypeEnum.MAGIC_DAMAGE)) {
            float reduction = BuffUtil.getRate(0F, BuffTypeEnum.MAGIC_DAMAGE, from);
            this.damage.set(Math.round(this.damage.get() * (1 - reduction)));
        }
        // 判断此技能是否能生效
        if (extraBattleProcessList.stream().anyMatch(ext -> !ext.canEffect(this))) {
            return;
        }
        // 命中插入结算
        extraBattleProcessList.forEach(ext -> ext.ifHit(this));
        nowSkill.getThisBehaviorExtraProcess().ifHit(this);
        skillEffect.getTemplate().ifHit(this);
        // 造成伤害
        BattleUtil.doDamage(this);
        // 有伤害倍率的技能 插入文描
        if (skillEffect.getDamageRate() > 0) {
            builder.append("，对").append(tarOrganism.getName()).append("造成").append(this.damage.get()).append("点伤害");
        }
        // 若对方仍有存活，且出手方身上有反伤buff，计算反伤
        List<BattleBuffDTO> reflectDamageBuffList = from.getBuffMap().get(BuffTypeEnum.REFLECT_DAMAGE);
        if (CollectionUtil.isNotEmpty(reflectDamageBuffList)
                && enemy.stream().anyMatch(battleDTO ->
                battleDTO.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0)) {
            for (BattleBuffDTO battleBuffDTO : reflectDamageBuffList) {
                long realDamage = NumberUtil.multiply(this.damage.get(), battleBuffDTO.getBuffDTO().getRate());
                BattleUtil.doRealDamage(from, realDamage, builder);
            }
        }
        if (tarOrganism.getHpWithAddition() <= 0) {
            // 触发受到致命伤害后的结算
            extraBattleProcessList.stream().filter(ext -> tar.equals(ext.getFrom()))
                    .forEach(ext -> ext.afterMeDeath(this));
            extraBattleProcessList.stream().filter(ext -> from.equals(ext.getFrom()))
                    .forEach(ext -> ext.afterTargetDeath(this));
        }
    }

    public void changeFieldEffect(FieldEffectEnum newEffect) {
        if (Objects.nonNull(this.getBattleRound().getBattleField().getFieldEffectEnum())) {
            this.getBattleRound().getBuilder()
                    .append(String.format("，场地效果【%s】失效",
                            this.getBattleRound().getBattleField().getFieldEffectEnum().getFieldEffectName()));
            this.getBattleRound().getBattleField().getFieldEffectEnum().getEffect().finalizeEffect(this);
        }
        this.getBattleRound().getBuilder()
                .append(String.format("，场地效果【%s】生效",
                        this.getBattleRound().getBattleField().getFieldEffectEnum().getFieldEffectName()));
        newEffect.getEffect().finalizeEffect(this);
        this.getBattleRound().getBattleField().setFieldEffectEnum(newEffect);
    }
}
