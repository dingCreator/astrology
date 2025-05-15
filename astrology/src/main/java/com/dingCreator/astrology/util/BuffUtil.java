package com.dingCreator.astrology.util;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.dto.battle.*;
import com.dingCreator.astrology.enums.BuffTypeEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * buff工具
 *
 * @author ding
 * @date 2024/2/2
 */
public class BuffUtil {

    /**
     * 给目标添加buff
     *
     * @param target 目标
     */
    public static void addBuff(BattleDTO from, BattleDTO target, BuffDTO buffDTO, StringBuilder builder) {
        addBuff(from, target, buffDTO, 1000, builder);
    }

    /**
     * 给目标添加buff
     *
     * @param target 目标
     */
    public static void addBuff(BattleDTO from, BattleDTO target, BuffDTO buffDTO, Integer round, StringBuilder builder) {
        if (target.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() <= 0) {
            return;
        }
        if (buffDTO.getAbnormal() && (target.getBuffMap().containsKey(BuffTypeEnum.IMMUNITY))) {
            builder.append("，免疫异常【").append(buffDTO.getBuffName()).append("】");
            return;
        }
        List<BattleBuffDTO> buffList = target.getBuffMap().getOrDefault(buffDTO.getBuffType(), new ArrayList<>());
        BattleBuffDTO now = BattleBuffDTO.builder().buffFrom(from).buffDTO(buffDTO).round(round).build();
        if (!buffDTO.getBuffName().isEmpty()) {
            // 有特殊名称的buff，会有覆盖策略
            buffDTO.getBuffOverrideStrategyEnum().getDoOverride().accept(now, buffList);
        } else {
            buffList.add(now);
        }
        builder.append("，").append(target.getOrganismInfoDTO().getOrganismDTO().getName());
        // 当buff有名称且之前的文描没有包含此段描述
        if (!buffDTO.getBuffName().isEmpty() && !builder.toString().contains(buffDTO.getBuffName())) {
            builder.append("附加<").append(buffDTO.getBuffName()).append(">，");
        }
        if (BuffTypeEnum.BuffEffectTypeEnum.PROPERTY.equals(buffDTO.getBuffType().getBuffEffectTypeEnum())) {
            builder.append(buffDTO.getBuffType().getChnDesc());
            if (buffDTO.getValue() != 0) {
                builder.append(buffDTO.getValue() > 0 ? "+" + buffDTO.getValue() : buffDTO.getValue());
            }
            if (buffDTO.getRate().floatValue() != 0) {
                float absVal = buffDTO.getRate().multiply(BigDecimal.valueOf(100)).floatValue();
                builder.append(buffDTO.getRate().floatValue() > 0 ? "+" + absVal : absVal).append("%");
            }
        } else if (BuffTypeEnum.BuffEffectTypeEnum.MARK.equals(buffDTO.getBuffType().getBuffEffectTypeEnum())) {
            builder.append(buffDTO.getBuffName());
        }
        // 最多300回合，比这个多的可以认为是持续整场的buff，无需显示持续多少回合
        if (round <= 300) {
            builder.append("(").append(round).append(")");
        }
        target.getBuffMap().put(buffDTO.getBuffType(), buffList);
    }

    /**
     * 给目标添加buff
     *
     * @param target 目标
     */
    public static void addBuff(BattleDTO from, BattleDTO target, List<GiveBuffDTO> buffList, StringBuilder builder) {
//        if (target.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() <= 0) {
//            return;
//        }
//        builder.append("，").append(target.getOrganismInfoDTO().getOrganismDTO().getName());
//        Set<String> abnormalNameSet = new HashSet<>();
//        Map<BuffTypeEnum, List<GiveBuffDTO>> buffTypeMap = buffList.stream()
//                .filter(buff -> {
//                    if (buff.getBuffDTO().getAbnormal() && (target.getBuffMap().containsKey(BuffTypeEnum.IMMUNITY))) {
//                        abnormalNameSet.add(buff.getBuffDTO().getBuffName());
//                        return false;
//                    }
//                    return true;
//                })
//                .collect(Collectors.groupingBy(buff -> buff.getBuffDTO().getBuffType()));
//        if (CollectionUtil.isNotEmpty(abnormalNameSet)) {
//            String abnormalName = abnormalNameSet.stream().reduce((s1, s2) -> s1 + "，" + s2).orElse("");
//            builder.append("免疫异常【").append(abnormalName).append("】");
//        }
//        Set<String> buffNameSet = new HashSet<>();
//        List<String> buffDescList = new ArrayList<>();
//        // 给目标添加buff
//        buffTypeMap.forEach((buffType, giveBuffList) -> {
//            List<BattleBuffDTO> buffDTOList = target.getBuffMap().getOrDefault(buffType, new ArrayList<>());
//            giveBuffList.forEach(giveBuff -> {
//                BattleBuffDTO now = BattleBuffDTO.builder().buffFrom(from).buffDTO(giveBuff.getBuffDTO()).round(giveBuff.getRound()).build();
//                if (!giveBuff.getBuffDTO().getBuffName().isEmpty()) {
//                    // 有特殊名称的buff，会有覆盖策略
//                    giveBuff.getBuffDTO().getBuffOverrideStrategyEnum().getDoOverride().accept(now, buffDTOList);
//                    buffNameSet.add(giveBuff.getBuffDTO().getBuffName());
//                } else {
//                    buffDTOList.add(now);
//                }
//
//            });
//            target.getBuffMap().put(buffType, buffDTOList);
//        });
//        // 当buff有名称
//        if (CollectionUtil.isNotEmpty(buffNameSet)) {
//            builder.append("附加<").append(buffNameSet.stream().reduce((s1, s2) -> s1 + "，" + s2).orElse(""))
//                    .append(">");
//        }
//
//
//
//
//
//
//        List<BattleBuffDTO> buffList = target.getBuffMap().getOrDefault(buffDTO.getBuffType(), new ArrayList<>());
//        BattleBuffDTO now = BattleBuffDTO.builder().buffFrom(from).buffDTO(buffDTO).round(round).build();
//        if (!buffDTO.getBuffName().isEmpty()) {
//            // 有特殊名称的buff，会有覆盖策略
//            buffDTO.getBuffOverrideStrategyEnum().getDoOverride().accept(now, buffList);
//        } else {
//            buffList.add(now);
//        }
//
//
//        if (BuffTypeEnum.BuffEffectTypeEnum.PROPERTY.equals(buffDTO.getBuffType().getBuffEffectTypeEnum())) {
//            builder.append(buffDTO.getBuffType().getChnDesc());
//            if (buffDTO.getValue() != 0) {
//                builder.append(buffDTO.getValue() > 0 ? "+" + buffDTO.getValue() : buffDTO.getValue());
//            }
//            if (buffDTO.getRate().floatValue() != 0) {
//                float absVal = buffDTO.getRate().multiply(BigDecimal.valueOf(100)).floatValue();
//                builder.append(buffDTO.getRate().floatValue() > 0 ? "+" + absVal : absVal).append("%");
//            }
//        } else if (BuffTypeEnum.BuffEffectTypeEnum.MARK.equals(buffDTO.getBuffType().getBuffEffectTypeEnum())) {
//            builder.append(buffDTO.getBuffName());
//        }
//        // 最多300回合，比这个多的可以认为是持续整场的buff，无需显示持续多少回合
//        if (round <= 300) {
//            builder.append("，持续").append(round).append("回合");
//        }
//        target.getBuffMap().put(buffDTO.getBuffType(), buffList);
    }


    /**
     * 数值类buff
     *
     * @param buffList buff列表
     * @param val      初始值
     * @return 数值类buff后的值
     */
    private static Long buffVal(List<BattleBuffDTO> buffList, Long val) {
        if (CollectionUtil.isEmpty(buffList)) {
            return val;
        }
        long buffVal = buffList.stream().mapToLong(buff -> buff.getBuffDTO().getValue()).reduce(val, Long::sum);
        return buffVal < 0 ? 0 : buffVal;
    }

    /**
     * 比例类buff
     *
     * @param buffList buff列表
     * @param val      初始值
     * @return 比例类buff后的值
     */
    private static Long buffRate(List<BattleBuffDTO> buffList, Long val) {
        if (CollectionUtil.isEmpty(buffList)) {
            return val;
        }
        long buffVal = BigDecimal.valueOf(val)
                .multiply(buffList.stream().map(buff -> buff.getBuffDTO().getRate()).reduce(BigDecimal.ONE, BigDecimal::add))
                .setScale(0, RoundingMode.HALF_UP).longValue();
        return buffVal < 0 ? 0 : buffVal;
    }

    /**
     * 比例类buff
     *
     * @param buffList buff列表
     * @param rate     初始值
     * @return 比例类buff后的值
     */
    private static Float buffRate(List<BattleBuffDTO> buffList, Float rate) {
        if (CollectionUtil.isEmpty(buffList)) {
            return rate;
        }
        return buffList.stream().map(buff -> buff.getBuffDTO().getRate())
                .reduce(new BigDecimal(String.valueOf(rate)), BigDecimal::add).floatValue();
    }

    /**
     * 根据buff名称获取累计buff
     *
     * @param name buff名称
     * @return buff
     */
    public static BuffDTO getBuffByName(String name, Map<BuffTypeEnum, List<BattleBuffDTO>> buffMap) {
        return null;
    }

    /**
     * 根据buff类型获取累计buff
     *
     * @param buffTypeEnum buff类型
     * @return buff
     */
    public static BuffDTO getBuffByType(BuffTypeEnum buffTypeEnum, Map<BuffTypeEnum, List<BattleBuffDTO>> buffMap) {
        List<BattleBuffDTO> list = buffMap.getOrDefault(buffTypeEnum, new ArrayList<>());
        return list.stream().map(BattleBuffDTO::getBuffDTO).reduce((buff1, buff2) -> {
            buff2.setValue(buff1.getValue() + buff2.getValue());
            buff2.setRate(buff1.getRate().add(buff2.getRate()));
            return buff2;
        }).orElse(new BuffDTO(buffTypeEnum, "tmp", 0L, 0F));
    }

    public static Long getVal(long val, BuffTypeEnum buffTypeEnum, BattleDTO battleDTO) {
        List<BattleBuffDTO> buffList = battleDTO.getBuffMap().get(buffTypeEnum);
        return buffRate(buffList, buffVal(buffList, val));
    }

    public static Float getRate(Float rate, BuffTypeEnum buffTypeEnum, BattleDTO battleDTO) {
        List<BattleBuffDTO> buffList = battleDTO.getBuffMap().get(buffTypeEnum);
        return buffRate(buffList, rate);
    }

    /**
     * 清除异常Buff
     *
     * @param battleDTO 战斗信息包装
     */
    public static void clearAbnormalBuff(BattleDTO battleDTO, StringBuilder builder) {
        battleDTO.getBuffMap().forEach((buffTypeEnum, buffList) -> buffList.removeIf(buff -> buff.getBuffDTO().getAbnormal()));
        builder.append("，").append(battleDTO.getOrganismInfoDTO().getOrganismDTO().getName()).append("所有负面状态被清除");
    }

    /**
     * 清除增益Buff
     *
     * @param battleDTO 战斗信息包装
     * @param builder   文本描述
     */
    public static void clearActiveBuff(BattleDTO battleDTO, StringBuilder builder) {
        battleDTO.getBuffMap().forEach((buffTypeEnum, buffList) -> {
            if (buffTypeEnum.getBuffEffectTypeEnum().equals(BuffTypeEnum.BuffEffectTypeEnum.PROPERTY)) {
                buffList.removeIf(buff -> buff.getBuffDTO().getRate().floatValue() > 0 || buff.getBuffDTO().getValue() > 0);
            }
        });
        builder.append("，").append(battleDTO.getOrganismInfoDTO().getOrganismDTO().getName()).append("所有增益被清除");
    }

    /**
     * 清除减益Buff
     *
     * @param battleDTO 战斗信息包装
     * @param builder   文本描述
     */
    public static void clearInactiveBuff(BattleDTO battleDTO, StringBuilder builder) {
        battleDTO.getBuffMap().forEach((buffTypeEnum, buffList) -> {
            if (buffTypeEnum.getBuffEffectTypeEnum().equals(BuffTypeEnum.BuffEffectTypeEnum.PROPERTY)) {
                buffList.removeIf(buff -> buff.getBuffDTO().getRate().floatValue() < 0 || buff.getBuffDTO().getValue() < 0);
            }
        });
        builder.append("，").append(battleDTO.getOrganismInfoDTO().getOrganismDTO().getName()).append("所有减益被清除");
    }

    /**
     * 清除所有Buff
     *
     * @param battleDTO 战斗信息包装
     * @param builder   文本描述
     */
    public static void clearAllBuff(BattleDTO battleDTO, StringBuilder builder) {
        battleDTO.setBuffMap(new HashMap<>());
        builder.append("，").append(battleDTO.getOrganismInfoDTO().getOrganismDTO().getName()).append("所有buff被清除");
    }

    /**
     * 计算无敌
     *
     * @param battleEffect 战斗信息包装
     * @return 是否无敌
     */
    public static boolean calInvincible(BattleEffectDTO battleEffect) {
        List<BattleBuffDTO> invincibleBuffList = battleEffect.getTar().getBuffMap().get(BuffTypeEnum.INVINCIBLE);
        if (CollectionUtil.isNotEmpty(invincibleBuffList)) {
            BattleBuffDTO buff = invincibleBuffList.stream().min(Comparator.comparing(BattleBuffDTO::getRound)).orElse(null);
            if (Objects.nonNull(buff)) {
                StringBuilder builder = battleEffect.getBattleRound().getBuilder();
                builder.append("，").append(battleEffect.getTar().getOrganismInfoDTO().getOrganismDTO().getName())
                        .append("状态：").append(buff.getBuffDTO().getBuffName()).append("，无法造成伤害");
                long times = buff.getBuffDTO().getValue();
                if (times > 0) {
                    --times;
                    if (times == 0) {
                        battleEffect.getTar().getBuffMap().get(BuffTypeEnum.INVINCIBLE).remove(buff);
                    } else {
                        buff.getBuffDTO().setValue(times);
                    }
                    builder.append("，").append("剩余次数：").append(times);
                }
                return true;
            }
        }
        return false;
    }
}
