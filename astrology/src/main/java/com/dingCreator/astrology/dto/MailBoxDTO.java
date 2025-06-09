package com.dingCreator.astrology.dto;

import com.alibaba.fastjson.JSONObject;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.entity.MailBox;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author ding
 * @date 2025/5/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailBoxDTO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 玩家ID
     */
    private Long playerId;

    /**
     * 主题
     */
    private String subject;

    /**
     * 物品信息
     */
    private ArticleItemDTO item;

    /**
     * 数量
     */
    private Integer itemCnt;

    /**
     * 发放时间
     */
    private LocalDateTime createTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireIn;

    public MailBox convert2Entity() {
        return MailBox.builder().id(this.id).playerId(this.playerId).subject(this.subject)
                .createTime(this.createTime).expireIn(this.expireIn).itemCnt(this.itemCnt)
                .itemJson(JSONObject.toJSONString(this.item)).build();
    }
}
