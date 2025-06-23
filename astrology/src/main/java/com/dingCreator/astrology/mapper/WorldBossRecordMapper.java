package com.dingCreator.astrology.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dingCreator.astrology.dto.WorldBossRankRecordDTO;
import com.dingCreator.astrology.entity.WorldBossRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ding
 * @date 2024/8/12
 */
@Mapper
public interface WorldBossRecordMapper extends BaseMapper<WorldBossRecord> {

    WorldBossRankRecordDTO queryRankByPlayerId(@Param("playerId") Long playerId, @Param("worldBossId") Long worldBossId);

    List<WorldBossRankRecordDTO> queryRank(@Param("worldBossId") Long worldBossId, @Param("index") int index, @Param("pageSize") int pageSize);
}
