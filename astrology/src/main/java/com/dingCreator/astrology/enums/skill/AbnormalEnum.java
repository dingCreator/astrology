package com.dingCreator.astrology.enums.skill;

import com.dingCreator.astrology.dto.battle.BattleDTO;
import com.dingCreator.astrology.dto.battle.BuffDTO;
import com.dingCreator.astrology.enums.EffectTypeEnum;
import com.dingCreator.astrology.util.BuffUtil;
import lombok.*;

import java.util.function.Consumer;

/**
 * @author ding
 * @date 2025/7/11
 */
@Getter
@AllArgsConstructor
public enum AbnormalEnum {

    ABSENT_MINDED("ABSENT_MINDED", "失神", "降低50%命中", input -> {
        BuffDTO buff = new BuffDTO(EffectTypeEnum.HIT, "失神", -0.5F, true);
        BuffUtil.addBuff(input.getFrom(), input.getTar(), buff, input.getRound(), input.getBuilder());
    }),

    ECHO("ECHO", "回音", "造成伤害时受到造成伤害量15%的真实伤害（反伤）", input -> {
        BuffDTO buff = new BuffDTO(EffectTypeEnum.REFLECT_DAMAGE, "回音", true);
        BuffUtil.addBuff(input.getFrom(), input.getTar(), buff, input.getRound(), input.getBuilder());
    }),

    MIST("MIST", "雾霭", "受到雾霭的侵蚀而降低10%速度", input -> {
        BuffDTO buff = new BuffDTO(EffectTypeEnum.SPEED, "雾霭", -0.1F, true);
        BuffUtil.addBuff(input.getFrom(), input.getTar(), buff, input.getRound(), input.getBuilder());
    }),

    VERTIGO("VERTIGO", "眩晕", "无法行动", input -> {
        BuffDTO buff = new BuffDTO(EffectTypeEnum.PAUSE, "眩晕", true);
        BuffUtil.addBuff(input.getFrom(), input.getTar(), buff, input.getRound(), input.getBuilder());
    }),

    BURN_DOWN("BURN_DOWN", "焚毁", "降低敌方30%生命回复效果且每回合扣除其最大生命值的8%", input -> {
        BuffDTO buff1 = new BuffDTO(EffectTypeEnum.HEAL, "焚毁", -0.3F, true);
        BuffDTO buff2 = new BuffDTO(EffectTypeEnum.BLEEDING, "焚毁", -0.08F, true);
        BuffUtil.addBuff(input.getFrom(), input.getTar(), buff1, input.getRound(), input.getBuilder());
        BuffUtil.addBuff(input.getFrom(), input.getTar(), buff2, input.getRound(), input.getBuilder());
    }),

    FEAR("FEAR", "恐惧", "无法行动，但速度提升10%", input -> {
        StringBuilder builder = input.getBuilder();
        BuffUtil.addBuff(input.getFrom(), input.getTar(),
                new BuffDTO(EffectTypeEnum.PAUSE, "恐惧", true), input.getRound(), builder);
        BuffUtil.addBuff(input.getFrom(), input.getTar(),
                new BuffDTO(EffectTypeEnum.SPEED, "恐惧", 0.1F, true), input.getRound(), builder);
    }),

    SUBMIT("SUBMIT", "臣服", "造成的物理和法术伤害减少100%", input -> {
        BuffUtil.addBuff(input.getFrom(), input.getTar(),
                new BuffDTO(EffectTypeEnum.DO_DAMAGE, "臣服", -1F, true), input.getRound(),
                input.getBuilder());
        BuffUtil.addBuff(input.getFrom(), input.getTar(),
                new BuffDTO(EffectTypeEnum.DO_MAGIC_DAMAGE, "臣服", -1F, true), input.getRound(),
                input.getBuilder());
    }),

    DROWNING("DROWNING", "溺水", "造成的物理与法术伤害减少30%且每回合扣除自身最大血量的8%", input -> {
        BuffUtil.addBuff(input.getFrom(), input.getTar(),
                new BuffDTO(EffectTypeEnum.DO_DAMAGE, "溺水", -0.3F, true), input.getRound(),
                input.getBuilder());
        BuffUtil.addBuff(input.getFrom(), input.getTar(),
                new BuffDTO(EffectTypeEnum.DO_MAGIC_DAMAGE, "溺水", -0.3F, true), input.getRound(),
                input.getBuilder());
        BuffUtil.addBuff(input.getFrom(), input.getTar(),
                new BuffDTO(EffectTypeEnum.BLEEDING, "溺水", -0.08F, true), input.getRound(),
                input.getBuilder());
    }),

    NUMB("NUMB", "麻痹", "敌人闪避与速度降低25%且无法行动持续一回合", input -> {
        BuffUtil.addBuff(input.getFrom(), input.getTar(),
                new BuffDTO(EffectTypeEnum.PAUSE, "麻痹", true),
                1, input.getBuilder());
        BuffUtil.addBuff(input.getFrom(), input.getTar(),
                new BuffDTO(EffectTypeEnum.DODGE, "麻痹", -0.25F, true),
                1, input.getBuilder());
        BuffUtil.addBuff(input.getFrom(), input.getTar(),
                new BuffDTO(EffectTypeEnum.SPEED, "麻痹", -0.25F, true),
                1, input.getBuilder());
    }),

    FREEZE("FREEZE", "冻结", "", input -> {

    }),

    POISONING("POISONING", "中毒", "", input -> {
        BuffUtil.addBuff(input.getFrom(), input.getTar(),
                new BuffDTO(EffectTypeEnum.ATK, "中毒", -0.1F, true),
                input.getRound(), input.getBuilder());
        BuffUtil.addBuff(input.getFrom(), input.getTar(),
                new BuffDTO(EffectTypeEnum.BLEEDING, "中毒", -0.05F, true),
                input.getRound(), input.getBuilder());
    }),
    ;

    private final String code;

    private final String name;

    private final String description;

    private final Consumer<AbnormalInput> effect;

    public void doEffect(AbnormalInput input) {
        input.getBuilder().append("，").append(input.getTar().getOrganismInfoDTO().getOrganismDTO().getName())
                .append("附加异常<").append(this.name).append(">");
        effect.accept(input);
    }

    public static AbnormalEnum getByCode(String code) {
        for (AbnormalEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AbnormalInput {
        private BattleDTO from;
        private BattleDTO tar;
        private Integer round;
        private StringBuilder builder;
    }
}
