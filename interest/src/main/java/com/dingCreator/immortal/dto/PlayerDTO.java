package com.dingCreator.immortal.dto;

import com.dingCreator.immortal.entity.Player;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huangkd
 * @date 2023/4/18
 */
@Data
public class PlayerDTO implements Serializable {
    /**
     * 玩家基础属性
     */
    private Player player;
    /**
     * 是否在小队中
     */
    private Boolean team;
}
