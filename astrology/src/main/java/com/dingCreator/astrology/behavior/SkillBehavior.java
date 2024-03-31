package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.SkillCache;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.entity.Player;
import com.dingCreator.astrology.entity.SkillBarItem;
import com.dingCreator.astrology.entity.SkillBelongTo;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.exception.SkillExceptionEnum;
import com.dingCreator.astrology.enums.job.JobEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.service.SkillBarItemService;
import com.dingCreator.astrology.service.SkillBelongToService;
import com.dingCreator.astrology.util.SkillUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/21
 */
public class SkillBehavior {

    /**
     * 获取技能循环链
     *
     * @param belongToId 归属ID
     * @return 技能循环链表头
     */
    public SkillBarDTO getSkillBarDTO(Long belongToId) {
        return SkillCache.getSkillBarItem(BelongToEnum.Player.getBelongTo(), belongToId);
    }

    /**
     * 创建新技能栏
     *
     * @param belongToId 归属ID
     * @param skillIds   技能ID列表
     */
    public void createSkillBarItem(Long belongToId, List<Long> skillIds) {
        Player player = PlayerCache.getPlayerById(belongToId).getPlayer();
        List<Long> validSkillIds = SkillBelongToService.querySkillBelongToBySkillId(BelongToEnum.Player.getBelongTo(),
                belongToId, skillIds).stream().map(SkillBelongTo::getSkillId).collect(Collectors.toList());
        if (skillIds.stream().distinct().count() != validSkillIds.size()) {
            throw SkillExceptionEnum.INVALID_SKILL_ID.getException();
        }

        validSkillIds.forEach(skillId -> {
            SkillEnum skillEnum = SkillEnum.getById(skillId);
            if (!skillEnum.getActive()) {
                throw SkillExceptionEnum.INVALID_SKILL_ID.getException();
            }
            List<String> jobEnumList = skillEnum.getJobCode();
            if (jobEnumList.contains("None")) {
                throw SkillExceptionEnum.JOB_SKILL_NOT_ALLOW.getException();
            } else if (jobEnumList.contains("All")) {
                return;
            } else if (!jobEnumList.contains(player.getJob())) {
                throw SkillExceptionEnum.JOB_SKILL_NOT_ALLOW.getException();
            }
        });

        SkillCache.deleteSkillBar(belongToId);
        SkillBarItemService.deleteSkillBarItem(BelongToEnum.Player.getBelongTo(), belongToId);
        SkillBarItemService.addSkillBarItem(SkillUtil.buildSkillBarItemChain(skillIds, BelongToEnum.Player, belongToId));
        SkillCache.deleteSkillBar(belongToId);
    }

    /**
     * 新增技能归属记录
     *
     * @param belongToId 归属ID
     * @param skillId    技能ID
     */
    public void createSkillBelongTo(Long belongToId, Long skillId) {
        SkillBelongToService.createSkillBelongTo(BelongToEnum.Player.getBelongTo(), belongToId, skillId);
    }

    /**
     * 获取技能列表
     *
     * @param belongToId 归属ID
     * @return 技能列表
     */
    public List<SkillBelongTo> getSkillBelongTo(Long belongToId) {
        return SkillBelongToService.querySkillBelongToList(BelongToEnum.Player.getBelongTo(), belongToId);
    }

    /**
     * 根据技能名称获取技能
     *
     * @param skillName 技能名称
     * @return 技能信息
     */
    public SkillEnum getSkillEnumByName(String skillName) {
        SkillEnum skillEnum = SkillEnum.getByName(skillName);
        if (Objects.isNull(skillEnum)) {
            throw SkillExceptionEnum.SKILL_NAME_NOT_EXIST.getException();
        }
        return skillEnum;
    }

    /**
     * 根据技能ID获取技能
     *
     * @return 技能信息
     */
    public SkillEnum getSkillEnumById(Long skillId) {
        SkillEnum skillEnum = SkillEnum.getById(skillId);
        if (Objects.isNull(skillEnum)) {
            throw SkillExceptionEnum.SKILL_ID_NOT_EXIST.getException();
        }
        return skillEnum;
    }

    private static class Holder {
        private static final SkillBehavior BEHAVIOR = new SkillBehavior();
    }

    private SkillBehavior() {

    }

    public static SkillBehavior getInstance() {
        return SkillBehavior.Holder.BEHAVIOR;
    }
}
