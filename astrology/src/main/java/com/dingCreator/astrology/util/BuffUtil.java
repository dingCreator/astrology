package com.dingCreator.astrology.util;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.astrology.dto.BattleDTO;
import com.dingCreator.astrology.dto.BattleBuffDTO;
import com.dingCreator.astrology.dto.BuffDTO;
import com.dingCreator.astrology.enums.BuffTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 战斗数值计算
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
    public static void addBuff(BattleDTO target, BuffDTO buffDTO, Integer round, StringBuilder builder) {
        List<BattleBuffDTO> buffList = target.getBuffMap().getOrDefault(buffDTO.getBuffType(), new ArrayList<>());
        BattleBuffDTO now = BattleBuffDTO.builder().buffDTO(buffDTO).round(round).build();
        if (buffDTO.getBuffName().length() > 0) {
            // 有特殊名称的buff，会有覆盖策略
            buffDTO.getBuffOverrideStrategyEnum().getDoOverride().accept(now, buffList);
        } else {
            buffList.add(now);
        }

        builder.append("，").append(target.getOrganismInfoDTO().getOrganismDTO().getName())
                .append(buffDTO.getBuffType().getChnDesc());
        if (buffDTO.getValue() != 0) {
            builder.append(buffDTO.getValue() > 0 ? "增加了" : "减少了").append(buffDTO.getValue());
        }
        if (buffDTO.getRate() != 0) {
            builder.append(buffDTO.getRate() > 0 ? "增加了" : "减少了").append(Math.abs(buffDTO.getRate()) * 100).append("%");
        }
        target.getBuffMap().put(buffDTO.getBuffType(), buffList);
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
        long buffVal = Math.round(val * (1 + buffList.stream().mapToDouble(buff -> buff.getBuffDTO().getRate())
                .reduce(1, Double::sum)));
        return buffVal < 0 ? 0 : buffVal;
    }

    /**
     * 获取物攻
     *
     * @param val     原数值
     * @param buffMap buff列表
     * @return 物攻
     */
    public static Long getAtk(long val, Map<BuffTypeEnum, List<BattleBuffDTO>> buffMap) {
        List<BattleBuffDTO> atkBuffList = buffMap.get(BuffTypeEnum.ATK);
        return buffRate(atkBuffList, buffVal(atkBuffList, val));
    }

    /**
     * 获取物防
     *
     * @param val     原数值
     * @param buffMap buff列表
     * @return 物防
     */
    public static Long getDef(long val, Map<BuffTypeEnum, List<BattleBuffDTO>> buffMap) {
        List<BattleBuffDTO> defBuffList = buffMap.get(BuffTypeEnum.DEF);
        return buffRate(defBuffList, buffVal(defBuffList, val));
    }

    /**
     * 获取魔攻
     *
     * @param val     原数值
     * @param buffMap buff列表
     * @return 魔攻
     */
    public static Long getMagicAtk(long val, Map<BuffTypeEnum, List<BattleBuffDTO>> buffMap) {
        List<BattleBuffDTO> magicAtkBuffList = buffMap.get(BuffTypeEnum.MAGIC_ATK);
        return buffRate(magicAtkBuffList, buffVal(magicAtkBuffList, val));
    }

    /**
     * 获取魔防
     *
     * @param val     原数值
     * @param buffMap buff列表
     * @return 魔防
     */
    public static Long getMagicDef(long val, Map<BuffTypeEnum, List<BattleBuffDTO>> buffMap) {
        List<BattleBuffDTO> magicDefBuffList = buffMap.get(BuffTypeEnum.MAGIC_DEF);
        return buffRate(magicDefBuffList, buffVal(magicDefBuffList, val));
    }

    /**
     * 获取速度
     *
     * @param val     原数值
     * @param buffMap buff列表
     * @return 速度
     */
    public static Long getSpeed(long val, Map<BuffTypeEnum, List<BattleBuffDTO>> buffMap) {
        List<BattleBuffDTO> speedBuffList = buffMap.get(BuffTypeEnum.SPEED);
        return buffRate(speedBuffList, buffVal(speedBuffList, val));
    }

    /**
     * 获取命中值
     *
     * @param val     原数值
     * @param buffMap buff列表
     * @return 速度
     */
    public static Long getHit(long val, Map<BuffTypeEnum, List<BattleBuffDTO>> buffMap) {
        List<BattleBuffDTO> hitBuffList = buffMap.get(BuffTypeEnum.HIT);
        return buffRate(hitBuffList, buffVal(hitBuffList, val));
    }

    /**
     * 获取闪避值
     *
     * @param val     原数值
     * @param buffMap buff列表
     * @return 速度
     */
    public static Long getDodge(long val, Map<BuffTypeEnum, List<BattleBuffDTO>> buffMap) {
        List<BattleBuffDTO> dodgeBuffList = buffMap.get(BuffTypeEnum.DODGE);
        return buffRate(dodgeBuffList, buffVal(dodgeBuffList, val));
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
            buff2.setRate(buff1.getRate() + buff2.getRate());
            return buff2;
        }).orElse(new BuffDTO(buffTypeEnum, "tmp", 0L, 0F));
    }
}
