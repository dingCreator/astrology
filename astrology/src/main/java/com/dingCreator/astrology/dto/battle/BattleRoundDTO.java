package com.dingCreator.astrology.dto.battle;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.dto.skill.SkillEffectDTO;
import com.dingCreator.astrology.enums.EffectTypeEnum;
import com.dingCreator.astrology.enums.FieldEffectEnum;
import com.dingCreator.astrology.enums.OrganismPropertiesEnum;
import com.dingCreator.astrology.enums.skill.DamageTypeEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.util.*;
import com.dingCreator.astrology.util.template.ExtraBattleProcessTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ding
 * @date 2025/4/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleRoundDTO implements Serializable {

    /**
     * 本回合行动者
     */
    private BattleDTO from;

    /**
     * 本回合行动者友方
     */
    private List<BattleDTO> our;

    /**
     * 本回合行动者敌方
     */
    private List<BattleDTO> enemy;

    /**
     * 战斗信息
     */
    private StringBuilder builder;

    /**
     * 战场
     */
    private BattleFieldDTO battleField;

    /**
     * 执行回合
     */
    public void executeRound() {
        List<String> battleMsg = this.getBattleField().getBattleMsg();
        // 初始化战斗过程记录
        builder = new StringBuilder("-回合").append(this.getBattleField().getRound()).append("->");
        beforeRound();
        inRound();
        afterRound();
        // 写入战斗信息
        battleMsg.add(builder.toString());
    }

    /**
     * 回合前
     */
    public void beforeRound() {
        // buff轮次-1 并清除所有过期buff
        from.getBuffMap().values().stream()
                .peek(buffList -> buffList.forEach(buff -> buff.setRound(buff.getRound() - 1)))
                .forEach(buffList -> buffList.removeIf(buffDTO -> buffDTO.getRound() < 0));
        // 插入结算
        List<ExtraBattleProcessTemplate> extraBattleProcessList = this.getBattleField().getExtraBattleProcessTemplateList();
        // 每回合
        extraBattleProcessList.forEach(ext -> ext.executeBeforeRound(this));
    }

    /**
     * 回合后
     */
    public void afterRound() {
        // 计算流血效果
        if (from.getBuffMap().containsKey(EffectTypeEnum.BLEEDING)) {
            from.getBuffMap().get(EffectTypeEnum.BLEEDING)
                    .forEach(battleBuff -> {
                        builder.append("，状态：").append(battleBuff.getBuffDTO().getBuffName());
                        BigDecimal bleedingRate = battleBuff.getBuffDTO().getRate();
                        bleedingRate = bleedingRate.compareTo(BigDecimal.ZERO) > 0 ?
                                bleedingRate : bleedingRate.multiply(BigDecimal.valueOf(-1));
                        long maxHp = from.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition();
                        long damage = bleedingRate.multiply(BigDecimal.valueOf(maxHp)).longValue();
                        long def = Math.round(0.2 * BattleUtil.getLongProperty(
                                from.getOrganismInfoDTO().getOrganismDTO().getDef(),
                                OrganismPropertiesEnum.DEF.getFieldName(),
                                from, this.battleField
                        ));
                        damage -= def;
                        damage = Math.min(damage, 100000);
                        BattleEffectDTO effectDTO = BattleEffectDTO.builder()
                                .from(battleBuff.getBuffFrom()).tar(from)
                                .damage(new AtomicLong(damage))
                                .damageTypeEnum(DamageTypeEnum.SPECIAL)
                                .battleRound(this).build();
                        BattleUtil.doDamage(effectDTO);
                    });
        }
        from.getRuleList().stream().filter(rule -> EffectTypeEnum.BLEEDING.equals(rule.getEffectTypeEnum()))
                    .forEach(rule -> {
                        builder.append("，状态：").append(rule.getRuleName());
                        BigDecimal bleedingRate = rule.getRate();
                        bleedingRate = bleedingRate.compareTo(BigDecimal.ZERO) > 0 ?
                                bleedingRate : bleedingRate.multiply(BigDecimal.valueOf(-1));
                        long maxHp = from.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition();
                        long damage = bleedingRate.multiply(BigDecimal.valueOf(maxHp)).longValue();
                        long def = Math.round(0.2 * BattleUtil.getLongProperty(
                                from.getOrganismInfoDTO().getOrganismDTO().getDef(),
                                OrganismPropertiesEnum.DEF.getFieldName(),
                                from, this.battleField
                        ));
                        damage -= def;
                        damage = Math.min(damage, 100000);
                        BattleEffectDTO effectDTO = BattleEffectDTO.builder()
                                .from(rule.getFrom()).tar(from)
                                .damage(new AtomicLong(damage))
                                .damageTypeEnum(DamageTypeEnum.SPECIAL)
                                .battleRound(this).build();
                        BattleUtil.doDamage(effectDTO);
                    });
        // 总轮次+1
        battleField.setRound(battleField.getRound() + 1);
        // 角色轮次+1
        from.setRound(from.getRound() + 1);
        // 计算行动值
        from.setBehavior(from.getBehavior() - battleField.getTotalBehavior());
        // 场地效果
        if (Objects.nonNull(this.getBattleField().getFieldEffectEnum())) {
            this.getBattleField().getFieldEffectEnum().getEffect().afterRound(this);
        }
        // 插入结算
        List<ExtraBattleProcessTemplate> extraBattleProcessList = this.getBattleField().getExtraBattleProcessTemplateList();
        // 每回合
        extraBattleProcessList.forEach(ext -> ext.executeAfterRound(this));
        // 移除阵亡召唤物
        our.removeIf(b -> b.getSummoned() && b.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() <= 0);
        enemy.removeIf(b -> b.getSummoned() && b.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() <= 0);
    }

    /**
     * 本回合
     */
    public void inRound() {
        if (from.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() <= 0) {
            return;
        }
        if (builder.lastIndexOf(">") != builder.length() - 1) {
            builder.append("，");
        }
        builder.append(from.getOrganismInfoDTO().getOrganismDTO().getName());
        // 获取技能栏
        SkillBarDTO bar = from.getOrganismInfoDTO().getSkillBarDTO();
        // 技能轮转
        from.getOrganismInfoDTO().setSkillBarDTO(Objects.isNull(bar.getNext()) ? bar.getHead() : bar.getNext());
        // 计算暂停效果
        List<BattleBuffDTO> pauseBuff = from.getBuffMap().get(EffectTypeEnum.PAUSE);
        if (CollectionUtils.isNotEmpty(pauseBuff)) {
            String pauseBuffName = pauseBuff.stream()
                    .map(battleBuff -> battleBuff.getBuffDTO().getBuffName() + battleBuff.getRound() + "回合")
                    .reduce((name1, name2) -> name1 + "，" + name2).orElse("未知");
            builder.append("状态：").append(pauseBuffName);
            return;
        }
        // 获取技能信息
        SkillEnum skillEnum = SkillEnum.getById(bar.getSkillId());
        // 扣取mp
        long mp = from.getOrganismInfoDTO().getOrganismDTO().getMpWithAddition();
        // mp < 0表示无限mp
        if (mp >= 0) {
            if (mp < skillEnum.getMp()) {
                builder.append("蓝量不足");
                // 蓝不够 替换为默认技能 若没有默认技能 则原地罚站
                if ((skillEnum = from.getOrganismInfoDTO().getDefaultSkill()) == null) {
                    builder.append("，使用技能失败");
                    return;
                }
                builder.append("，改为");
            } else {
                mp -= skillEnum.getMp();
                from.getOrganismInfoDTO().getOrganismDTO().setMpWithAddition(mp);
            }
        }
        builder.append("使用技能【").append(skillEnum.getName(this)).append("】");
        builder.append("，蓝量：").append(from.getOrganismInfoDTO().getOrganismDTO().getMpWithAddition())
                .append("/").append(from.getOrganismInfoDTO().getOrganismDTO().getMaxMpWithAddition());
        // 轮次开始前插入结算
        skillEnum.getThisBehaviorExtraProcess().beforeBehavior(this);
        final SkillEnum nowSkill = skillEnum;
        skillEnum.getSkillEffects().forEach(skillEffect -> executeSingleEffectBehavior(skillEffect, nowSkill));
        // 显示敌方血量
        enemy.forEach(e -> builder.append("，").append(e.getOrganismInfoDTO().getOrganismDTO().getName())
                .append("血量：").append(e.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition())
                .append("/").append(e.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition()));
        // 轮次结束后插入结算
        skillEnum.getThisBehaviorExtraProcess().afterBehavior(this);
        // 记录此回合详细数据
        BattleRoundRecordDTO record = BattleRoundRecordDTO.builder()
                .from(CopyUtil.copyNewInstance(this.getFrom()))
                .our(CopyUtil.copyNewInstance(this.getOur()))
                .enemy(CopyUtil.copyNewInstance(this.getEnemy()))
                .battleFieldRecord(
                        BattleFieldRecordDTO.builder()
                                .round(battleField.getRound())
                                .maxRound(battleField.getMaxRound())
                                .totalBehavior(battleField.getTotalBehavior())
                                .fieldEffectEnum(CopyUtil.copyNewInstance(battleField.getFieldEffectEnum()))
                                .build()
                ).build();
        this.battleField.getRoundRecordList().add(record);
    }


    /**
     * 单个技能效果
     *
     * @param skillEffect 技能效果
     * @param nowSkill    当前技能
     */
    public void executeSingleEffectBehavior(SkillEffectDTO skillEffect, SkillEnum nowSkill) {
        // 选择技能目标
        List<BattleDTO> target = skillEffect.getTargetEnum().getTarget().getTarget(this.from, this.our, this.enemy);
        if (target.isEmpty()) {
            return;
        }
        target.forEach(tar -> {
            BattleEffectDTO battleEffect = BattleEffectDTO.builder()
                    .from(from).tar(tar).our(our).enemy(enemy)
                    .damageRate(BigDecimal.valueOf(skillEffect.getDamageRate()))
                    .nowSkill(nowSkill).skillEffect(skillEffect).battleRound(this).build();
            battleEffect.executeSingleTarBehavior();
        });
    }

    public void changeFieldEffect(FieldEffectEnum newEffect) {
        if (Objects.nonNull(this.getBattleField().getFieldEffectEnum())) {
            builder.append(String.format("，场地效果【%s】失效", this.getBattleField().getFieldEffectEnum().getFieldEffectName()));
            this.getBattleField().getFieldEffectEnum().getEffect().finalizeEffect(this);
        }
        builder.append(String.format("，场地效果【%s】生效", newEffect.getFieldEffectName()));
        newEffect.getEffect().finalizeEffect(this);
        this.getBattleField().setFieldEffectEnum(newEffect);
    }
}
