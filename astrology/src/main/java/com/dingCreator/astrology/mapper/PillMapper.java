package com.dingCreator.astrology.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dingCreator.astrology.entity.Pill;
import org.apache.ibatis.annotations.Param;

/**
 * @author ding
 * @date 2025/3/11
 */
public interface PillMapper extends BaseMapper<Pill> {

    Pill getPill(@Param("vigor") Integer vigor, @Param("warn") Integer warn, @Param("cold") Integer cold,
                 @Param("toxicity") Integer toxicity, @Param("quality") Integer quality, @Param("star") Integer star);
}
