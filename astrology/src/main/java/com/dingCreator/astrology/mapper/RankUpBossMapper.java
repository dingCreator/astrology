package com.dingCreator.astrology.mapper;

import com.dingCreator.astrology.entity.RankUpBoss;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ding
 * @date 2024/3/29
 */
@Mapper
public interface RankUpBossMapper {

    /**
     * 获取突破boss
     *
     * @param job  职业
     * @param rank 突破前阶级
     * @return 突破boss信息
     */
    @Select("select * from astrology_rank_up_boss where job=#{job} and rank=#{rank}")
    List<RankUpBoss> getRankUpBoss(@Param("job") String job, @Param("rank") Integer rank);
}
