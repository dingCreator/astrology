package com.dingCreator.astrology.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dingCreator.astrology.entity.Player;
import org.apache.ibatis.annotations.*;

/**
 * @author ding
 * @date 2023/4/19
 */
@Mapper
public interface PlayerDataMapper extends BaseMapper<Player> {

    /**
     * 根据ID获取玩家
     *
     * @param id 玩家ID
     * @return 玩家信息
     */
    @Select("select * from astrology_player where id=#{id}")
    Player getPlayerById(@Param("id") Long id);

    /**
     * 根据名称获取玩家
     *
     * @param name 玩家名称
     * @return 玩家信息
     */
    @Select("select * from astrology_player where `name`=#{name}")
    Player getPlayerByName(@Param("name") String name);

    /**
     * 新增玩家
     *
     * @param player 玩家基本信息
     * @return 是否新增成功
     */
    @Insert("INSERT INTO astrology_player (id, name, hp, max_hp, mp, max_mp, atk, magic_atk, def, magic_def, penetrate, " +
            "magic_penetrate, critical_rate, critical_reduction_rate, critical_damage, critical_damage_reduction, " +
            "behavior_speed, hit, dodge, life_stealing, `rank`, `level`, `exp`, job, map_id, status, status_start_time, enabled" +
            ") VALUES (" +
            "#{id}, #{name}, #{hp}, #{maxHp}, #{mp}, #{maxMp}, #{atk}, #{magicAtk}, #{def}, #{magicDef}, #{penetrate}," +
            "#{magicPenetrate}, #{criticalRate}, #{criticalReductionRate}, #{criticalDamage}, #{criticalDamageReduction}, " +
            "#{behaviorSpeed}, #{hit}, #{dodge}, #{lifeStealing}, #{rank}, #{level}, #{exp}, #{job}, #{mapId}," +
            "#{status}, #{statusStartTime}, #{enabled})")
    Boolean createPlayer(Player player);
}
