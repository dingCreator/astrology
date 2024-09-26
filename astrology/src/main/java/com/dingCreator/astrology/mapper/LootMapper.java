package com.dingCreator.astrology.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dingCreator.astrology.entity.Loot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
     * @param belongTo       归属
     * @param belongToIdList 归属ID
     * @return 掉落物
     */
    @Select({"<script>",
            "select * from astrology_loot where belong_to=#{belongTo} and belong_to_id IN(",
            "<foreach collection='belongToIdList' item='belongToId' index='belongToId' separator=','>",
            "#{belongToId}",
            "</foreach>)",
            "</script>"})
    List<Loot> getByBelongToId(@Param("belongTo") String belongTo, @Param("belongToIdList") List<Long> belongToIdList);
}
