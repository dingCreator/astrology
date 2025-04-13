package com.dingCreator.astrology.behavior;

import com.dingCreator.astrology.cache.PlayerCache;
import com.dingCreator.astrology.cache.SkillCache;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.dto.organism.player.PlayerDTO;
import com.dingCreator.astrology.dto.skill.SkillBarDTO;
import com.dingCreator.astrology.entity.SkillBag;
import com.dingCreator.astrology.entity.SkillBelongTo;
import com.dingCreator.astrology.enums.BelongToEnum;
import com.dingCreator.astrology.enums.exception.SkillExceptionEnum;
import com.dingCreator.astrology.enums.skill.SkillEnum;
import com.dingCreator.astrology.response.PageResponse;
import com.dingCreator.astrology.service.SkillBagService;
import com.dingCreator.astrology.service.SkillBarItemService;
import com.dingCreator.astrology.service.SkillBelongToService;
import com.dingCreator.astrology.util.PageUtil;
import com.dingCreator.astrology.util.SkillUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/2/21
 */
public class SkillBehavior {

    private final SkillBarItemService skillBarItemService = SkillBarItemService.getInstance();

    private final SkillBelongToService skillBelongToService = SkillBelongToService.getInstance();

    /**
     * 获取技能循环链
     *
     * @param belongToId 归属ID
     * @return 技能循环链表头
     */
    public SkillBarDTO getSkillBarDTO(Long belongToId) {
        return SkillCache.getSkillBarItem(BelongToEnum.PLAYER.getBelongTo(), belongToId);
    }

    /**
     * 创建新技能栏
     *
     * @param belongToId 归属ID
     * @param skillIds   技能ID列表
     */
    public void createSkillBarItem(Long belongToId, List<Long> skillIds) {
        PlayerDTO playerDTO = PlayerCache.getPlayerById(belongToId).getPlayerDTO();
        List<Long> validSkillIds = skillBelongToService.querySkillBelongToBySkillId(BelongToEnum.PLAYER.getBelongTo(),
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
            if (jobEnumList.contains(Constants.NONE)) {
                throw SkillExceptionEnum.JOB_SKILL_NOT_ALLOW.getException();
            } else if (!jobEnumList.contains(playerDTO.getJob())) {
                throw SkillExceptionEnum.JOB_SKILL_NOT_ALLOW.getException();
            }
        });

        SkillCache.deleteSkillBar(belongToId);
        skillBarItemService.deleteSkillBarItem(BelongToEnum.PLAYER.getBelongTo(), belongToId);
        skillBarItemService.addSkillBarItem(SkillUtil.buildSkillBarItemChain(skillIds, BelongToEnum.PLAYER, belongToId));
        SkillCache.deleteSkillBar(belongToId);
    }

    /**
     * 新增技能归属记录
     *
     * @param belongToId 归属ID
     * @param skillId    技能ID
     */
    public void createSkillBelongTo(Long belongToId, Long skillId) {
        skillBelongToService.createSkillBelongTo(BelongToEnum.PLAYER.getBelongTo(), belongToId, skillId);
    }

    /**
     * 学习技能
     *
     * @param playerId 玩家ID
     * @param idOrName 技能ID或技能名称
     */
    public SkillEnum learnSkill(Long playerId, String idOrName) {
        PlayerDTO player = PlayerCache.getPlayerById(playerId).getPlayerDTO();
        SkillEnum skillEnum = SkillUtil.analyseSkill(idOrName);
        if (Objects.isNull(skillEnum)) {
            throw SkillExceptionEnum.CANT_ANALYSE_SKILL.getException(idOrName);
        }
        List<String> jobCodes = skillEnum.getJobCode();
        if (jobCodes.contains(Constants.NONE)) {
            throw SkillExceptionEnum.JOB_SKILL_NOT_ALLOW_2.getException();
        } else if (!jobCodes.contains(player.getJob())) {
            throw SkillExceptionEnum.JOB_SKILL_NOT_ALLOW_2.getException();
        }
        SkillBagService.getInstance().learnSkill(playerId, skillEnum.getId());
        return skillEnum;
    }

    /**
     * 技能背包
     *
     * @param playerId  玩家ID
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return 技能背包
     */
    public PageResponse<SkillBag> pageSkillBag(Long playerId, int pageIndex, int pageSize) {
        int count = SkillBagService.getInstance().countSkillBag(playerId);
        List<SkillBag> list = SkillBagService.getInstance().pageSkillBag(playerId, pageIndex, pageSize);
        return PageUtil.addPageDesc(list, pageIndex, pageSize, count);
    }

    /**
     * 获取技能列表
     *
     * @param belongToId 归属ID
     * @return 技能列表
     */
    public List<SkillBelongTo> getSkillBelongTo(Long belongToId) {
        return skillBelongToService.querySkillBelongToList(BelongToEnum.PLAYER.getBelongTo(), belongToId);
    }

    /**
     * 根据技能名称获取技能
     *
     * @param skillName 技能名称
     * @return 技能信息
     */
    public SkillEnum getSkillEnumByName(String skillName) {
        return SkillEnum.getByName(skillName);
    }

    /**
     * 根据技能ID获取技能
     *
     * @return 技能信息
     */
    public SkillEnum getSkillEnumById(Long skillId) {
        return SkillEnum.getById(skillId);
    }

    /**
     * 获取技能列表
     *
     * @param pageSize  每页条数
     * @param pageIndex 页码
     * @return 技能列表
     */
    public PageResponse<SkillEnum> listSkillEnum(int pageIndex, int pageSize) {
        return PageUtil.buildPage(SkillEnum.values(), pageIndex, pageSize);
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
