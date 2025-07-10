package com.dingCreator.astrology.request;

import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.enums.activity.SignAwardTypeEnum;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @author ding
 * @date 2025/7/5
 */
@Data
public class SignActivityAwardSettingReq extends ActivityAwardSettingReq {

    private Map<SignAwardTypeEnum, Set<ArticleItemDTO>> signAwardMap;
}
