package com.dingCreator.astrology.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dingCreator.astrology.dto.MailBoxDTO;
import com.dingCreator.astrology.dto.article.ArticleItemDTO;
import com.dingCreator.astrology.util.ArticleUtil;
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
@TableName("astrology_mail_box")
public class MailBox {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 玩家ID
     */
    @TableField("player_id")
    private Long playerId;

    /**
     * 主题
     */
    @TableField("subject")
    private String subject;

    /**
     * 物品信息
     */
    @TableField("item_json")
    private String itemJson;

    /**
     * 数量
     */
    @TableField("item_cnt")
    private Integer itemCnt;

    /**
     * 发放时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 过期时间
     */
    @TableField("expire_in")
    private LocalDateTime expireIn;

    /**
     * 是否已领取
     */
    @TableField("received")
    private Boolean received;

    public static final String PLAYER_ID = "player_id";

    public static final String EXPIRE_IN = "expire_in";

    public static final String RECEIVED = "received";

    public MailBoxDTO convert2Dto() {
        return MailBoxDTO.builder().id(this.id).playerId(this.playerId).subject(this.subject)
                .createTime(this.createTime).expireIn(this.expireIn).itemCnt(this.itemCnt)
                .item(ArticleUtil.convert(this.itemJson)).build();
    }
}
