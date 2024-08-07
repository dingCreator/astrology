package com.dingCreator.astrology.vo;

import com.dingCreator.astrology.dto.LootItemDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ding
 * @date 2024/4/11
 */
@Data
public class LootVO implements Serializable {
    /**
     * 货币
     */
    private Long money;
    /**
     * 经验值
     */
    private Long exp;
    /**
     * 掉落物名称集合
     */
    private List<String> lootItemNameList;

    public LootVO() {
        this.money = 0L;
        this.exp = 0L;
        this.lootItemNameList = new ArrayList<>();
    }
}
