package com.dingCreator.astrology.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface DungeonRecordMapper extends BaseMapper<DungeonRecord> {

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
            "select * from astrology_dungeon_record where player_id IN (",
            "<foreach collection='playerIdList' item='playerId' index='playerId' separator=','>",
            "#{playerId}",
            "</foreach>",
            ") and dungeon_id=#{dungeonId}",
            "</script>"})
    List<DungeonRecord> queryList(@Param("playerIdList") List<Long> playerIdList, @Param("dungeonId") Long dungeonId);

    /**
     * 插入新记录
     *
     * @param dungeonRecord 探索记录
     * @return rows
     */
    @Insert("insert into astrology_dungeon_record(player_id,dungeon_id,last_explore_time) values"
            + "(#{playerId},#{dungeonId},#{lastExploreTime})")
    Integer createRecord(DungeonRecord dungeonRecord);

    /**
     * 更新记录
     *
     * @param dungeonRecord 探索记录
     * @return rows
     */
    @Insert("update astrology_dungeon_record set last_explore_time=#{lastExploreTime} where "
            + "player_id=#{playerId} and dungeon_id=#{dungeonId}")
    Integer updateRecord(DungeonRecord dungeonRecord);
}
