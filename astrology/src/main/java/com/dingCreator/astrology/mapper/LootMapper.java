package com.dingCreator.astrology.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dingCreator.astrology.dto.loot.LootQueryDTO;
import com.dingCreator.astrology.entity.Loot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ding
 * @date 2024/4/11
 */
@Mapper
public interface LootMapper extends BaseMapper<Loot> {

    /**
     * 获取掉落物
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 掉落物
     */
    List<LootQueryDTO> getByBelongToId(@Param("belongTo") String belongTo, @Param("belongToId") Long belongToId);

    /**
     * 获取掉落物
     *
     * @param belongTo       归属
     * @param belongToIdList 归属ID
     * @return 掉落物
     */
    List<LootQueryDTO> getByBelongToIdList(@Param("belongTo") String belongTo, @Param("belongToIdList") List<Long> belongToIdList);
}
