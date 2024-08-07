package com.dingCreator.astrology.mapper;

import com.dingCreator.astrology.entity.DungeonRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ding
 * @date 2024/4/7
 */
@Mapper
public interface DungeonRecordMapper {

    /**
     * 查询
     *
     * @param playerId  玩家ID
     * @param dungeonId 副本ID
     * @return rows
     */
    @Select("select * from astrology_dungeon_record where playerId=#{playerId} and dungeonId=#{dungeonId}")
    DungeonRecord query(@Param("playerId") Long playerId, @Param("dungeonId") Long dungeonId);

    /**
     * 查询
     *
     * @param playerIdList  玩家ID列表
     * @param dungeonId 副本ID
     * @return rows
     */
    @Select({"<script>",
            "select * from astrology_dungeon_record where playerId IN (",
            "<foreach collection='playerIdList' item='playerId' index='playerId' separator=','>",
            "#{playerId}",
            "</foreach>",
            ") and dungeonId=#{dungeonId}",
            "</script>"})
    List<DungeonRecord> queryList(@Param("playerIdList") List<Long> playerIdList, @Param("dungeonId") Long dungeonId);

    /**
     * 插入新记录
     *
     * @param dungeonRecord 探索记录
     * @return rows
     */
    @Insert("insert into astrology_dungeon_record(playerId,dungeonId,lastExploreTime) values"
            + "(#{playerId},#{dungeonId},#{lastExploreTime})")
    Integer createRecord(DungeonRecord dungeonRecord);

    /**
     * 更新记录
     *
     * @param dungeonRecord 探索记录
     * @return rows
     */
    @Insert("update astrology_dungeon_record set lastExploreTime=#{lastExploreTime} where "
            + "playerId=#{playerId} and dungeonId=#{dungeonId}")
    Integer updateRecord(DungeonRecord dungeonRecord);
}
