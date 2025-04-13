package com.dingCreator.astrology.dto.battle;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dingCreator.astrology.dto.organism.OrganismDTO;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2025/4/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleRoundDTO {

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
        builder = new StringBuilder("-->");
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
        // 插入结算
        List<ExtraBattleProcessTemplate> extraBattleProcessList = this.getBattleField().getExtraBattleProcessTemplateList();
        // 每回合
        extraBattleProcessList.forEach(ext -> ext.beforeEachRound(this));
        // 我方每回合
        extraBattleProcessList.stream().filter(ext -> ext.getOur().contains(from))
                .forEach(ext -> ext.beforeOurRound(this));
        // 敌方每回合
        extraBattleProcessList.stream().filter(ext -> ext.getEnemy().contains(from))
                .forEach(ext -> ext.beforeEnemyRound(this));
        // 我每回合
        extraBattleProcessList.stream().filter(ext -> from.equals(ext.getFrom()))
                .forEach(ext -> ext.beforeMyRound(this));
    }

    /**
     * 回合后
     */
    public void afterRound() {
        // 插入结算
        List<ExtraBattleProcessTemplate> extraBattleProcessList = this.getBattleField().getExtraBattleProcessTemplateList();
        // 战斗信息
        List<String> battleMsg = this.getBattleField().getBattleMsg();
        // 计算流血效果
        if (from.getBuffMap().containsKey(BuffTypeEnum.BLEEDING)) {
            from.getBuffMap().get(BuffTypeEnum.BLEEDING)
                    .forEach(battleBuff -> {
                        builder.append("状态：").append(battleBuff.getBuffDTO().getBuffName());
                        BigDecimal bleedingRate = battleBuff.getBuffDTO().getRate();
                        long maxHp = from.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition();
                        long damage = bleedingRate.multiply(BigDecimal.valueOf(maxHp)).longValue();
                        BattleEffectDTO effectDTO = BattleEffectDTO.builder()
                                .from(battleBuff.getBuffFrom()).tar(from)
                                .damage(new AtomicLong(damage)).battleRound(this).build();
                        BattleUtil.doDamage(effectDTO);
                    });
        }
        // 总轮次+1
        battleField.setRound(battleField.getRound() + 1);
        // 角色轮次+1
        from.setRound(from.getRound() + 1);
        // buff轮次-1 并清除所有过期buff
        from.getBuffMap().values().stream()
                .peek(buffList -> buffList.forEach(buff -> buff.setRound(buff.getRound() - 1)))
                .forEach(buffList -> buffList.removeIf(buffDTO -> buffDTO.getRound() < 0));
        from.setBehavior(from.getBehavior() - battleField.getTotalBehavior());
        // 我每回合
        extraBattleProcessList.stream().filter(ext -> from.equals(ext.getFrom()))
                .forEach(ext -> ext.afterMyRound(this));
        // 敌方每回合
        extraBattleProcessList.stream().filter(ext -> ext.getEnemy().contains(from))
                .forEach(ext -> ext.afterEnemyRound(this));
        // 我方每回合
        extraBattleProcessList.stream().filter(ext -> ext.getOur().contains(from))
                .forEach(ext -> ext.afterOurRound(this));
        // 每回合
        extraBattleProcessList.forEach(ext -> ext.afterEachRound(this));
    }

    /**
     * 本回合
     */
    public void inRound() {
        builder.append(from.getOrganismInfoDTO().getOrganismDTO().getName());
        // 获取技能栏
        SkillBarDTO bar = from.getOrganismInfoDTO().getSkillBarDTO();
        // 技能轮转
        from.getOrganismInfoDTO().setSkillBarDTO(Objects.isNull(bar.getNext()) ? bar.getHead() : bar.getNext());
        // 计算暂停效果
        List<BattleBuffDTO> pauseBuff = from.getBuffMap().get(BuffTypeEnum.PAUSE);
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
        skillEnum.getThisBehaviorExtraProcess().beforeThisRound(this);
        final SkillEnum nowSkill = skillEnum;
        skillEnum.getSkillEffects().forEach(skillEffect ->
                executeSingleEffectBehavior(skillEffect, nowSkill));
        enemy.forEach(e -> builder.append("，").append(e.getOrganismInfoDTO().getOrganismDTO().getName())
                .append("生命值：").append(e.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition())
                .append("/").append(e.getOrganismInfoDTO().getOrganismDTO().getMaxHpWithAddition()));
        // 轮次结束后插入结算
        skillEnum.getThisBehaviorExtraProcess().afterThisRound(this);
    }


    /**
     * 单个技能效果
     *
     * @param skillEffect 技能效果
     * @param nowSkill    当前技能
     */
    public void executeSingleEffectBehavior(SkillEffectDTO skillEffect, SkillEnum nowSkill) {
        // 选择技能目标
        List<BattleDTO> target = skillEffect.getSkillTargetEnum().getGetTarget().getTarget(this.from, this.our, this.enemy);
        if (target.isEmpty()) {
            return;
        }
        target.forEach(tar -> {
            BattleEffectDTO battleEffect = BattleEffectDTO.builder()
                    .from(from).tar(tar).our(our).enemy(enemy)
                    .damageRate(BigDecimal.valueOf(skillEffect.getDamageRate()))
                    .nowSkill(nowSkill).battleRound(this)
                    .build();
            battleEffect.executeSingleTarBehavior();
        });
    }

    public void changeFieldEffect(FieldEffectEnum newEffect) {
        if (Objects.nonNull(this.getBattleField().getFieldEffectEnum())) {
            builder.append(String.format("，场地效果【%s】失效", this.getBattleField().getFieldEffectEnum().getFieldEffectName()));
            this.getBattleField().getFieldEffectEnum().getEffect().finalizeEffect(this);
        }
        builder.append(String.format("，场地效果【%s】生效", this.getBattleField().getFieldEffectEnum().getFieldEffectName()));
        newEffect.getEffect().finalizeEffect(this);
        this.getBattleField().setFieldEffectEnum(newEffect);
    }
}
