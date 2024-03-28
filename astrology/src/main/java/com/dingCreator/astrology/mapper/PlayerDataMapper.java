package com.dingCreator.astrology.mapper;

import com.dingCreator.astrology.entity.Player;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;

/**
 * @author ding
 * @date 2023/4/19
 */
@Mapper
public interface PlayerDataMapper {

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
    @Select("select * from astrology_player where name=#{name}")
    Player getPlayerByName(@Param("name") String name);

    /**
     * 新增玩家
     *
     * @param player 玩家基本信息
     * @return 是否新增成功
     */
    @Insert("INSERT INTO astrology_player (id, name, hp, maxHp, mp, maxMp, atk, magicAtk, def, magicDef, penetrate, " +
            "magicPenetrate, criticalRate, criticalReductionRate, criticalDamage, criticalDamageReduction, " +
            "behaviorSpeed, hit, dodge, rank, level, exp, job, mapId, status, statusStartTime, enabled" +
            ") VALUES (" +
            "#{id}, #{name}, #{hp}, #{maxHp}, #{mp}, #{maxMp}, #{atk}, #{magicAtk}, #{def}, #{magicDef}, #{penetrate}," +
            "#{magicPenetrate}, #{criticalRate}, #{criticalReductionRate}, #{criticalDamage}, #{criticalDamageReduction}, " +
            "#{behaviorSpeed}, #{hit}, #{dodge}, #{rank}, #{level}, #{exp}, #{job}, #{mapId}," +
            "#{status}, #{statusStartTime}, #{enabled})")
    Boolean createPlayer(Player player);

    /**
     * 根据ID批量更新玩家数据
     *
     * @param player 新的玩家数据
     * @return 是否更新成功
     */
    @Update("<script>" +
            "UPDATE astrology_player" +
            "<set>" +
            "<if test='name != null'>name=#{name},</if>" +
            "<if test='hp != null'>hp=#{hp},</if>" +
            "<if test='maxHp != null'>maxHp=#{maxHp},</if>" +
            "<if test='mp != null'>mp=#{mp},</if>" +
            "<if test='maxMp != null'>maxMp=#{maxMp},</if>" +
            "<if test='atk != null'>atk=#{atk},</if>" +
            "<if test='magicAtk != null'>magicAtk=#{magicAtk},</if>" +
            "<if test='def != null'>def=#{def},</if>" +
            "<if test='magicDef != null'>magicDef=#{magicDef},</if>" +
            "<if test='penetrate != null'>penetrate=#{penetrate},</if>" +
            "<if test='magicPenetrate != null'>penetrate=#{magicPenetrate},</if>" +
            "<if test='criticalRate != null'>criticalRate=#{criticalRate},</if>" +
            "<if test='criticalReductionRate != null'>criticalReductionRate=#{criticalReductionRate},</if>" +
            "<if test='criticalDamage != null'>criticalDamage=#{criticalDamage},</if>" +
            "<if test='criticalDamageReduction != null'>criticalDamageReduction=#{criticalDamageReduction},</if>" +
            "<if test='behaviorSpeed != null'>behaviorSpeed=#{behaviorSpeed},</if>" +
            "<if test='hit != null'>hit=#{hit},</if>" +
            "<if test='dodge != null'>dodge=#{dodge},</if>" +
            "<if test='rank != null'>rank=#{rank},</if>" +
            "<if test='level != null'>level=#{level},</if>" +
            "<if test='exp != null'>exp=#{exp},</if>" +
            "<if test='job != null'>job=#{job},</if>" +
            "<if test='mapId != null'>mapId=#{mapId},</if>" +
            "<if test='status != null'>status=#{status},</if>" +
            "<if test='statusStartTime != null'>statusStartTime=#{statusStartTime},</if>" +
            "<if test='enabled != null'>enabled=#{enabled},</if>" +
            "</set>" +
            "WHERE id=#{id}" +
            "</script>")
    Boolean updateById(Player player);
}
