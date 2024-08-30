package com.dingCreator.astrology.entity.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author ding
 * @date 2024/2/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("astrology_monster")
public class Monster extends Organism {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    protected Long id;
    /**
     * 怪物描述
     */
    @TableField("description")
    protected String description;

    /**
     * 快速生成怪物
     * @param name 怪物名称
     * @return 怪物
     */
    public static Monster buildMonsterWithTemplate(String name) {
        Monster monster = new Monster();
        monster.setName(name);
        monster.setHp(100L);
        monster.setMaxHp(100L);
        monster.setMp(10L);
        monster.setMaxMp(10L);
        monster.setAtk(10L);
        monster.setMagicAtk(10L);
        monster.setDef(5L);
        monster.setMagicDef(5L);
        monster.setPenetrate(0F);
        monster.setMagicPenetrate(0F);
        monster.setBehaviorSpeed(10L);
        monster.setHit(100L);
        monster.setDodge(100L);
        monster.setCriticalDamage(2F);
        monster.setCriticalDamageReduction(0F);
        monster.setCriticalRate(0.1F);
        monster.setCriticalReductionRate(0F);
        monster.setLifeStealing(0F);
        monster.setLevel(1);
        monster.setRank(1);
        return monster;
    }
}
