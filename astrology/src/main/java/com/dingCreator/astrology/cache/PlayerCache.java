package com.dingCreator.astrology.cache;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.equipment.EquipmentBarDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.organism.player.PlayerInfoDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.entity.PlayerAsset;
import com.dingCreator.astrology.enums.AssetTypeEnum;
import com.dingCreator.astrology.enums.exception.PlayerExceptionEnum;
import com.dingCreator.astrology.service.PlayerService;
import com.dingCreator.astrology.util.LockUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/1
 */
public class PlayerCache {
    /**
     * 玩家信息缓存
     */
    private static final Map<Long, PlayerInfoDTO> PLAYER_MAP = new ConcurrentHashMap<>();

    /**
     * 创建新玩家
     *
     * @param player 新玩家
     */
    public static void createPlayer(Player player) {
        if (PLAYER_MAP.containsKey(player.getId())
                || Objects.nonNull(PlayerService.getInstance().getPlayerById(player.getId()))) {
            throw PlayerExceptionEnum.PLAYER_EXIST.getException();
        }
        LockUtil.execute(Constants.PLAYER_LOCK_PREFIX + player.getId(), () -> {
            if (PLAYER_MAP.containsKey(player.getId())
                    || Objects.nonNull(PlayerService.getInstance().getPlayerById(player.getId()))) {
                throw PlayerExceptionEnum.PLAYER_EXIST.getException();
            }
            List<PlayerAsset> assetList = Arrays.stream(AssetTypeEnum.values())
                    .map(e -> PlayerAsset.builder().playerId(player.getId()).assetType(e.getCode()).assetCnt(0L).build())
                    .collect(Collectors.toList());
            if (!PlayerService.getInstance().createPlayer(player, assetList)) {
                throw new IllegalStateException("创建角色失败");
            }
            PlayerInfoDTO playerInfoDTO = new PlayerInfoDTO();
            playerInfoDTO.setEquipmentBarDTO(new EquipmentBarDTO());
            playerInfoDTO.setTeam(false);
            playerInfoDTO.setAssetList(assetList.stream().map(PlayerAsset::convert).collect(Collectors.toList()));

            PlayerDTO playerDTO = new PlayerDTO();
            playerDTO.copyProperties(player);
            playerInfoDTO.setPlayerDTO(playerDTO);
            PLAYER_MAP.put(player.getId(), playerInfoDTO);
        });
    }

    /**
     * 懒加载机制读取玩家数据到缓存
     *
     * @param id 玩家ID
     * @return 玩家数据
     */
    public static PlayerInfoDTO getPlayerById(Long id) {
        PlayerInfoDTO playerInfoDTO = PLAYER_MAP.getOrDefault(id, null);
        if (Objects.isNull(playerInfoDTO)) {
            return LockUtil.execute(Constants.PLAYER_LOCK_PREFIX + id, () -> {
                PlayerInfoDTO temp = PLAYER_MAP.getOrDefault(id, null);
                if (Objects.isNull(temp)) {
                    temp = PlayerService.getInstance().getPlayerDTOById(id);
                    if (Objects.isNull(temp)) {
                        throw PlayerExceptionEnum.PLAYER_NOT_FOUND.getException();
                    }
                    if (!temp.getPlayerDTO().getEnabled()) {
                        throw PlayerExceptionEnum.PLAYER_DISABLED.getException();
                    }
                    PLAYER_MAP.put(id, temp);
                }
                return temp;
            });
        }
        return playerInfoDTO;
    }

    /**
     * 更新玩家数据
     *
     * @param id 玩家ID
     */
    public static void save(Long id) {
        save(Collections.singletonList(id));
    }

    /**
     * 更新玩家数据
     *
     * @param ids 玩家ID
     */
    public static void save(List<Long> ids) {
        List<Player> players = ids.stream()
                .map(PLAYER_MAP::get)
                .map(PlayerInfoDTO::getPlayerDTO)
                .map(playerDTO -> {
                    Player player = new Player();
                    playerDTO.copyProperties2Player(player);
                    return player;
                }).collect(Collectors.toList());
        PlayerService.getInstance().updatePlayerByIds(players);
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        PLAYER_MAP.clear();
    }
}
