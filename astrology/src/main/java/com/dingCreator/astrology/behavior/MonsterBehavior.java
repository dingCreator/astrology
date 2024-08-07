package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.entity.base.Monster;
import com.dingCreator.astrology.service.MonsterService;

import java.util.List;

/**
 * @author ding
 * @date 2024/4/15
 */
public class MonsterBehavior {

    /**
     * 查询怪物信息
     *
     * @param pageIndex 页码
     * @return 怪物列表
     */
    public List<Monster> listMonster(int pageIndex) {
        return MonsterService.listMonster(pageIndex, Constants.DEFAULT_PAGE_SIZE);
    }

    /**
     * 新增怪物
     *
     * @param monster 怪物
     */
    public void createMonster(Monster monster) {
        MonsterService.createMonster(monster);
    }

    /**
     * 修改怪物属性
     *
     * @param propertyType 属性类型
     * @param val          值
     */
    public void updateMonster(String propertyType, String val) {

    }

    private static class Holder {
        private static final MonsterBehavior BEHAVIOR = new MonsterBehavior();
    }

    private MonsterBehavior() {

    }

    public static MonsterBehavior getInstance() {
        return MonsterBehavior.Holder.BEHAVIOR;
    }
}
