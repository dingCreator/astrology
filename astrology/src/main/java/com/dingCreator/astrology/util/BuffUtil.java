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
    public static void addBuff(BattleDTO target, BuffDTO buffDTO, Integer round) {
        List<BattleBuffDTO> buffList = target.getBuffMap().getOrDefault(buffDTO.getBuffType(), new ArrayList<>());
        BattleBuffDTO battleBuffDTO = new BattleBuffDTO();
        battleBuffDTO.setBuffDTO(buffDTO);
        battleBuffDTO.setRound(round);
        buffList.add(battleBuffDTO);
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
        long buffVal = Math.round(val * buffList.stream().mapToDouble(buff -> buff.getBuffDTO().getRate())
                .reduce(1, Double::sum));
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
        List<BattleBuffDTO> atkRateBuffList = buffMap.get(BuffTypeEnum.ATK_RATE);
        return buffRate(atkRateBuffList, buffVal(atkBuffList, val));
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
        List<BattleBuffDTO> defRateBuffList = buffMap.get(BuffTypeEnum.DEF_RATE);
        return buffRate(defRateBuffList, buffVal(defBuffList, val));
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
        List<BattleBuffDTO> magicAtkRateBuffList = buffMap.get(BuffTypeEnum.MAGIC_ATK_RATE);
        return buffRate(magicAtkRateBuffList, buffVal(magicAtkBuffList, val));
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
        List<BattleBuffDTO> magicDefRateBuffList = buffMap.get(BuffTypeEnum.MAGIC_DEF_RATE);
        return buffRate(magicDefRateBuffList, buffVal(magicDefBuffList, val));
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
        List<BattleBuffDTO> speedRateBuffList = buffMap.get(BuffTypeEnum.SPEED_RATE);
        return buffRate(speedRateBuffList, buffVal(speedBuffList, val));
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
        List<BattleBuffDTO> hitRateBuffList = buffMap.get(BuffTypeEnum.HIT_RATE);
        return buffRate(hitRateBuffList, buffVal(hitBuffList, val));
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
        List<BattleBuffDTO> dodgeRateBuffList = buffMap.get(BuffTypeEnum.DODGE_RATE);
        return buffRate(dodgeRateBuffList, buffVal(dodgeBuffList, val));
    }
}
