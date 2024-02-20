package com.dingCreator.yuanshen.util;

import cn.hutool.core.collection.CollectionUtil;
import com.dingCreator.yuanshen.dto.BattleDTO;
import com.dingCreator.yuanshen.dto.BuffDTO;
import com.dingCreator.yuanshen.enums.BuffTypeEnum;

import java.util.ArrayList;
import java.util.List;

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
    public static void addBuff(BattleDTO target, BuffDTO buffDTO) {
        List<BuffDTO> buffList = target.getBuffMap().getOrDefault(buffDTO.getBuffType(), new ArrayList<>());
        buffList.add(buffDTO);
        target.getBuffMap().put(buffDTO.getBuffType(), buffList);
    }

    /**
     * 数值类buff
     *
     * @param buffList buff列表
     * @param val      初始值
     * @return 数值类buff后的值
     */
    private static Long buffVal(List<BuffDTO> buffList, Long val) {
        if (CollectionUtil.isEmpty(buffList)) {
            return val;
        }
        return buffList.stream().mapToLong(BuffDTO::getValue).reduce(val, Long::sum);
    }

    /**
     * 比例类buff
     *
     * @param buffList buff列表
     * @param val      初始值
     * @return 比例类buff后的值
     */
    private static Long buffRate(List<BuffDTO> buffList, Long val) {
        if (CollectionUtil.isEmpty(buffList)) {
            return val;
        }
        return Math.round(val * buffList.stream().mapToDouble(BuffDTO::getRate).reduce(1, Double::sum));
    }

    /**
     * 获取物攻
     *
     * @param battleDTO 战斗信息
     * @return 物攻
     */
    public static Long getAtk(BattleDTO battleDTO) {
        long atk = battleDTO.getOrganismDTO().getOrganism().getAtk();
        List<BuffDTO> atkBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.ATK.getName());
        List<BuffDTO> atkRateBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.ATK_RATE.getName());
        return buffRate(atkRateBuffList, buffVal(atkBuffList, atk));
    }

    /**
     * 获取物防
     *
     * @param battleDTO 战斗信息
     * @return 物防
     */
    public static Long getDef(BattleDTO battleDTO) {
        long def = battleDTO.getOrganismDTO().getOrganism().getDef();
        List<BuffDTO> defBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.DEF.getName());
        List<BuffDTO> defRateBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.DEF_RATE.getName());
        return buffRate(defRateBuffList, buffVal(defBuffList, def));
    }

    /**
     * 获取魔攻
     *
     * @param battleDTO 战斗信息
     * @return 魔攻
     */
    public static Long getMagicAtk(BattleDTO battleDTO) {
        long magicAtk = battleDTO.getOrganismDTO().getOrganism().getMagicAtk();
        List<BuffDTO> magicAtkBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.MAGIC_ATK.getName());
        List<BuffDTO> magicAtkRateBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.MAGIC_ATK_RATE.getName());
        return buffRate(magicAtkRateBuffList, buffVal(magicAtkBuffList, magicAtk));
    }

    /**
     * 获取魔防
     *
     * @param battleDTO 战斗信息
     * @return 魔防
     */
    public static Long getMagicDef(BattleDTO battleDTO) {
        long magicDef = battleDTO.getOrganismDTO().getOrganism().getMagicDef();
        List<BuffDTO> magicDefBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.MAGIC_DEF.getName());
        List<BuffDTO> magicDefRateBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.MAGIC_DEF_RATE.getName());
        return buffRate(magicDefRateBuffList, buffVal(magicDefBuffList, magicDef));
    }

    /**
     * 获取速度
     *
     * @param battleDTO 战斗信息
     * @return 速度
     */
    public static Long getSpeed(BattleDTO battleDTO) {
        long speed = battleDTO.getOrganismDTO().getOrganism().getBehaviorSpeed();
        List<BuffDTO> speedBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.SPEED.getName());
        List<BuffDTO> speedRateBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.SPEED_RATE.getName());
        return buffRate(speedRateBuffList, buffVal(speedBuffList, speed));
    }

    /**
     * 获取命中值
     *
     * @param battleDTO 战斗信息
     * @return 速度
     */
    public static Long getHit(BattleDTO battleDTO) {
        long hit = battleDTO.getOrganismDTO().getOrganism().getHit();
        List<BuffDTO> hitBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.HIT.getName());
        List<BuffDTO> hitRateBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.HIT_RATE.getName());
        return buffRate(hitRateBuffList, buffVal(hitBuffList, hit));
    }

    /**
     * 获取闪避值
     *
     * @param battleDTO 战斗信息
     * @return 速度
     */
    public static Long getDodge(BattleDTO battleDTO) {
        long dodge = battleDTO.getOrganismDTO().getOrganism().getDodge();
        List<BuffDTO> dodgeBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.DODGE.getName());
        List<BuffDTO> dodgeRateBuffList = battleDTO.getBuffMap().get(BuffTypeEnum.DODGE_RATE.getName());
        return buffRate(dodgeRateBuffList, buffVal(dodgeBuffList, dodge));
    }
}
