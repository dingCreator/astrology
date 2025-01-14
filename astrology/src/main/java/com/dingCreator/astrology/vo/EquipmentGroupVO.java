package com.dingCreator.astrology.vo;

import com.dingCreator.astrology.enums.equipment.EquipmentRankEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ding
 * @date 2024/4/24
 */
@Data
public class EquipmentGroupVO implements Serializable {
    /**
     * 装备名称
     */
    private String equipmentName;
    /**
     * 装备品级
     */
    private EquipmentRankEnum equipmentRank;
    /**
     * 限定职业
     */
    private List<String> limitJob;
    /**
     * 限制等级
     */
    private Integer limitLevel;
    /**
     * 装备数量
     */
    private Integer count;
    /**
     * 装备描述
     */
    private String desc;
}
