package com.dingCreator.astrology.dto.battle;

import com.dingCreator.astrology.enums.BuffTypeEnum;
import com.dingCreator.astrology.enums.FieldEffectEnum;
import com.dingCreator.astrology.enums.OrganismPropertiesEnum;
import com.dingCreator.astrology.util.BattleUtil;
import com.dingCreator.astrology.util.template.ExtraBattleProcessTemplate;
import com.dingCreator.astrology.vo.BattleResultVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author ding
 * @date 2025/4/2
 */
@Data
@Builder
@AllArgsConstructor
public class BattleFieldDTO implements Serializable {
    /**
     * 战斗发起方
     */
    private List<BattleDTO> initiatorList;
    /**
     * 战斗接受方
     */
    private List<BattleDTO> recipientList;
    /**
     * 场地效果
     */
    private FieldEffectEnum fieldEffectEnum;
    /**
     * 轮次
     */
    private Integer round;
    /**
     * 最大轮次
     */
    private final Integer maxRound;
    /**
     * 行动值总计
     */
    private Long totalBehavior;
    /**
     * 战斗信息
     */
    private List<String> battleMsg;
    /**
     * 战斗回合详情
     */
    private List<BattleRoundRecordDTO> roundRecordList;
    /**
     * 插入结算
     */
    private List<ExtraBattleProcessTemplate> extraBattleProcessTemplateList;

    public BattleResultVO startBattle() {
        battleMsg = new ArrayList<>(maxRound + 2);
        // 战斗前
        // 境界压制法则
        // 获取最高境界
        long initiatorHighestRank = initiatorList.stream()
                .mapToLong(b -> b.getOrganismInfoDTO().getOrganismDTO().getRank()).max().orElse(0L);
        long recipientHighestRank = recipientList.stream()
                .mapToLong(b -> b.getOrganismInfoDTO().getOrganismDTO().getRank()).max().orElse(0L);
        // 境界压制
        BattleUtil.getRankSuppression(initiatorList, recipientHighestRank, battleMsg);
        BattleUtil.getRankSuppression(recipientList, initiatorHighestRank, battleMsg);
        // 战斗前插入事件结算
        extraBattleProcessTemplateList.forEach(ext -> ext.executeBeforeBattle(this));
        // 总行动值
        this.totalBehavior = initiatorList.stream()
                .mapToLong(o -> BattleUtil.getLongProperty(
                        o.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed(),
                        OrganismPropertiesEnum.BEHAVIOR_SPEED.getFieldName(), o, this
                )).sum() +
                recipientList.stream()
                        .mapToLong(o -> BattleUtil.getLongProperty(
                                o.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed(),
                                OrganismPropertiesEnum.BEHAVIOR_SPEED.getFieldName(), o, this
                        )).sum();
        // 初始化轮次
        this.round = 1;
        // 战斗中
        while (initiatorList.stream().mapToLong(o -> o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition()).sum() > 0
                && recipientList.stream().mapToLong(o -> o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition()).sum() > 0
                && this.round <= this.maxRound) {
            battleProcess(initiatorList, recipientList);
            battleProcess(recipientList, initiatorList);
        }
        // 战斗后
        extraBattleProcessTemplateList.forEach(ext -> ext.executeAfterBattle(this));
        // 判断胜负
        long initiatorAliveNum = initiatorList.stream()
                .filter(o -> o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0).count();
        long recipientAliveNum = recipientList.stream()
                .filter(o -> o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0).count();
        // 构建返回数据
        BattleResultVO response = new BattleResultVO();
        response.setInfo(battleMsg);
        response.setRoundRecordList(roundRecordList);
        if (initiatorAliveNum > recipientAliveNum) {
            response.setBattleResult(BattleResultVO.BattleResult.WIN);
        } else if (initiatorAliveNum == recipientAliveNum) {
            response.setBattleResult(BattleResultVO.BattleResult.DRAW);
        } else {
            response.setBattleResult(BattleResultVO.BattleResult.LOSE);
        }
        return response;
    }

    /**
     * 战斗过程
     *
     * @param from 出手方
     * @param to   敌方
     */
    public void battleProcess(List<BattleDTO> from, List<BattleDTO> to) {
        // 判断对方是否全体阵亡
        if (to.stream().mapToLong(t -> t.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition()).sum() <= 0) {
            return;
        }
        from.stream()
                // 过滤已阵亡角色
                .filter(o -> o.getOrganismInfoDTO().getOrganismDTO().getHpWithAddition() > 0)
                // 计算行动值
                .peek(o -> {
                    long speed = o.getOrganismInfoDTO().getOrganismDTO().getBehaviorSpeed();
                    long behavior = o.getBehavior()
                            + BattleUtil.getLongProperty(speed, BuffTypeEnum.SPEED.getName(), o, this);
                    o.setBehavior(behavior);
                })
                // 行动值大于阈值的才行动
                .filter(o -> o.getBehavior() > this.getTotalBehavior())
                .forEach(o -> {
                    BattleRoundDTO round = BattleRoundDTO.builder().from(o).our(from).enemy(to).battleField(this).build();
                    round.executeRound();
                });
    }

    public void changeFieldEffect(FieldEffectEnum newEffect) {
        if (Objects.nonNull(this.fieldEffectEnum)) {
            battleMsg.add(String.format("场地效果【%s】失效", this.fieldEffectEnum.getFieldEffectName()));
            this.fieldEffectEnum.getEffect().finalizeEffect(this);
        }
        battleMsg.add(String.format("场地效果【%s】生效", newEffect.getFieldEffectName()));
        newEffect.getEffect().finalizeEffect(this);
        this.fieldEffectEnum = newEffect;
    }

    /**
     * 获取战场上某人的友方
     *
     * @param from 某人
     * @return 友方
     */
    public List<BattleDTO> getOur(BattleDTO from) {
        return initiatorList.contains(from) ? initiatorList : recipientList;
    }

    /**
     * 获取战场上某人的敌方
     *
     * @param from 某人
     * @return 敌方
     */
    public List<BattleDTO> getEnemy(BattleDTO from) {
        return initiatorList.contains(from) ? recipientList : initiatorList;
    }
}
