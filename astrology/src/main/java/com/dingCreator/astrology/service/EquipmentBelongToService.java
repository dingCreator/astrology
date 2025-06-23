package com.dingCreator.astrology.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingCreator.astrology.constants.Constants;
import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.equipment.EquipmentGroupQueryDTO;
import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.enums.exception.EquipmentExceptionEnum;
import com.dingCreator.astrology.mapper.EquipmentBelongToMapper;
import com.dingCreator.astrology.util.LockUtil;
import com.dingCreator.astrology.vo.EquipmentGroupVO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ding
 * @date 2024/4/10
 */
public class EquipmentBelongToService {

    /**
     * 根据ID查询信息
     *
     * @param id ID
     * @return 信息
     */
    public EquipmentBelongTo getById(Long id) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(EquipmentBelongToMapper.class).getById(id));
    }

    public EquipmentBelongTo getByNameAndLevel(String belongTo, Long belongToId, String equipmentName, int level) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(EquipmentBelongToMapper.class).selectOne(
                        new QueryWrapper<EquipmentBelongTo>()
                                .eq(EquipmentBelongTo.BELONG_TO, belongTo)
                                .eq(EquipmentBelongTo.BELONG_TO_ID, belongToId)
                                .eq(EquipmentBelongTo.EQUIPMENT_ID, EquipmentEnum.getByName(equipmentName).getId())
                                .eq(EquipmentBelongTo.EQUIPMENT_LEVEL, level)
                                .gt(EquipmentBelongTo.TOTAL_CNT, 0)
                )
        );
    }

    /**
     * 根据归属ID查询列表
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 装备列表
     */
    public List<EquipmentBelongTo> listByBelongToId(String belongTo, Long belongToId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(EquipmentBelongToMapper.class).listByBelongToId(belongTo, belongToId));
    }

    /**
     * 根据归属ID查询列表
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 装备列表
     */
    public EquipmentBelongTo getByBelongToIdAndEquipmentId(String belongTo, Long belongToId, Long equipmentId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(EquipmentBelongToMapper.class)
                        .selectOne(new QueryWrapper<EquipmentBelongTo>()
                                .eq(EquipmentBelongTo.BELONG_TO, belongTo)
                                .eq(EquipmentBelongTo.BELONG_TO_ID, belongToId)
                                .eq(EquipmentBelongTo.EQUIPMENT_ID, equipmentId)
                                .gt(EquipmentBelongTo.TOTAL_CNT, 0)
                        )
        );
    }

    /**
     * 根据归属ID查询列表
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 装备列表
     */
    public List<EquipmentGroupVO> listGroupByBelongToId(String belongTo, Long belongToId, List<Long> ids) {
        List<EquipmentBelongTo> belongToList = DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(EquipmentBelongToMapper.class).selectList(
                        new QueryWrapper<EquipmentBelongTo>()
                                .eq(EquipmentBelongTo.BELONG_TO, belongTo)
                                .eq(EquipmentBelongTo.BELONG_TO_ID, belongToId)
                                .in(CollectionUtil.isNotEmpty(ids), EquipmentBelongTo.EQUIPMENT_ID, ids)
                ));
        return belongToList.stream().map(query -> {
            EquipmentEnum equipmentEnum = EquipmentEnum.getById(query.getEquipmentId());
            EquipmentGroupVO vo = new EquipmentGroupVO();
            vo.setEquipmentRank(equipmentEnum.getEquipmentRankEnum());
            vo.setEquipmentTypeEnum(equipmentEnum.getEquipmentTypeEnum());
            vo.setEquipmentName(equipmentEnum.getName());
            vo.setLimitJob(equipmentEnum.getLimitJob());
            vo.setLimitLevel(equipmentEnum.getLimitLevel());
            vo.setCount(query.getTotalCnt());
            vo.setDesc(equipmentEnum.getDesc());
            return vo;
        }).sorted((e1, e2) -> {
            if (e2.getEquipmentRank().getRare().equals(e1.getEquipmentRank().getRare())) {
                return e2.getCount().compareTo(e1.getCount());
            }
            return e2.getEquipmentRank().getRare().compareTo(e1.getEquipmentRank().getRare());
        }).collect(Collectors.toList());
    }

    public int selectCount(String belongTo, Long belongToId, List<Long> ids) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession -> sqlSession.getMapper(EquipmentBelongToMapper.class)
                .selectCount(new QueryWrapper<EquipmentBelongTo>()
                        .eq(EquipmentBelongTo.BELONG_TO, belongTo)
                        .eq(EquipmentBelongTo.BELONG_TO_ID, belongToId)
                        .gt(EquipmentBelongTo.TOTAL_CNT, 0)
                        .in(CollectionUtil.isNotEmpty(ids), EquipmentBelongTo.EQUIPMENT_ID, ids)
                ));
    }

    /**
     * 根据归属ID查询是否已穿的装备
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @param equip      是否已装备
     * @return 装备列表
     */
    public List<EquipmentBelongTo> getBelongToIdEquip(String belongTo, Long belongToId, boolean equip) {
        int equipNum = equip ? 1 : 0;
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(EquipmentBelongToMapper.class).getBelongToIdEquip(belongTo, belongToId, equipNum));
    }

    /**
     * 新增所有权
     *
     * @param equipmentBelongTo 装备归属
     */
    public void addBelongTo(EquipmentBelongTo equipmentBelongTo) {
        DatabaseProvider.getInstance().transactionExecute(sqlSession ->
                LockUtil.execute(Constants.EQUIPMENT_LOCK_PREFIX + equipmentBelongTo +
                        Constants.UNDERLINE + equipmentBelongTo.getBelongToId(), () -> {
                    EquipmentBelongToMapper mapper = sqlSession.getMapper(EquipmentBelongToMapper.class);
                    EquipmentBelongTo oldEquipment = mapper.selectOne(new QueryWrapper<EquipmentBelongTo>()
                            .eq(EquipmentBelongTo.BELONG_TO, equipmentBelongTo.getBelongTo())
                            .eq(EquipmentBelongTo.BELONG_TO_ID, equipmentBelongTo.getBelongToId())
                            .eq(EquipmentBelongTo.EQUIPMENT_ID, equipmentBelongTo.getEquipmentId())
                            .eq(EquipmentBelongTo.EQUIPMENT_LEVEL, equipmentBelongTo.getEquipmentLevel())
                    );
                    if (Objects.nonNull(oldEquipment)) {
                        if (oldEquipment.getEquip() && oldEquipment.getTotalCnt() + equipmentBelongTo.getTotalCnt() < 1) {
                            throw EquipmentExceptionEnum.NOT_ENOUGH_EQUIPMENT.getException();
                        } else if (!oldEquipment.getEquip() && oldEquipment.getTotalCnt() + equipmentBelongTo.getTotalCnt() < 0) {
                            throw EquipmentExceptionEnum.NOT_ENOUGH_EQUIPMENT.getException();
                        }
                        oldEquipment.setTotalCnt(oldEquipment.getTotalCnt() + equipmentBelongTo.getTotalCnt());
                        mapper.updateById(oldEquipment);
                    } else {
                        if (equipmentBelongTo.getTotalCnt() < 0) {
                            throw EquipmentExceptionEnum.NOT_ENOUGH_EQUIPMENT.getException();
                        }
                        mapper.insert(equipmentBelongTo);
                    }
                })
        );
    }

    /**
     * 穿上/卸下装备
     *
     * @param id ID
     */
    public void updateEquipment(Long id, boolean equip) {
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(EquipmentBelongToMapper.class)
                .equipEquipment(id, equip ? 1 : 0));
    }

    /**
     * 所有权变更
     *
     * @param id         ID
     * @param belongTo   归属
     * @param belongToId 归属ID
     */
    public void updateBelongToId(Long id, String belongTo, Long belongToId) {
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(EquipmentBelongToMapper.class)
                .updateBelongToId(id, belongTo, belongToId));
    }

    /**
     * 删除所有权
     *
     * @param id ID
     */
    public void deleteById(Long id) {
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(EquipmentBelongToMapper.class)
                .deleteById(id));
    }


    private static class Holder {
        private static final EquipmentBelongToService SERVICE = new EquipmentBelongToService();
    }

    private EquipmentBelongToService() {

    }

    public static EquipmentBelongToService getInstance() {
        return EquipmentBelongToService.Holder.SERVICE;
    }
}
