package com.dingCreator.yuanshen.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 战斗包装类，用于存储一些中间过程
 * 血量/蓝量这一类要持久化的则直接修改生物属性内的值
 *
 * @author ding
 * @date 2024/2/2
 */
@Data
public class BattleDTO implements Serializable {
    /**
     * 生物属性
     */
    private OrganismDTO organismDTO;
    /**
     * 行动值
     */
    private Long behavior;
    /**
     * 获得的buff
     */
    private Map<String, List<BuffDTO>> buffMap;
}
