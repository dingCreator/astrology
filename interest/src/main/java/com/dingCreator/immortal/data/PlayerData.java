package com.dingCreator.immortal.data;

import com.dingCreator.immortal.dto.PlayerDTO;
import com.dingCreator.immortal.entity.Player;

import java.util.List;

/**
 * @author huangkd
 * @date 2023/4/19
 */
public interface PlayerData {
    /**
     * 根据ID获取玩家
     *
     * @param id 玩家ID
     * @return 玩家信息
     */
    PlayerDTO getPlayerById(Long id);

    /**
     * 根据ID批量更新玩家数据
     *
     * @param players 新玩家数据
     * @return 是否更新成功
     */
    Boolean batchUpdateById(List<Player> players);
}
