package com.dingCreator.astrology.service;

import com.dingCreator.astrology.database.DatabaseProvider;
import com.dingCreator.astrology.dto.equipment.EquipmentGroupQueryDTO;
import com.dingCreator.astrology.entity.EquipmentBelongTo;
import com.dingCreator.astrology.enums.equipment.EquipmentEnum;
import com.dingCreator.astrology.mapper.EquipmentBelongToMapper;
import com.dingCreator.astrology.vo.EquipmentGroupVO;

import java.util.List;
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
    public static EquipmentBelongTo getById(Long id) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(EquipmentBelongToMapper.class).getById(id));
    }

    /**
     * 根据归属ID查询列表
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 装备列表
     */
    public static List<EquipmentBelongTo> listByBelongToId(String belongTo, Long belongToId) {
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
    public static List<EquipmentBelongTo> getByBelongToIdAndEquipmentId(String belongTo, Long belongToId, Long equipmentId) {
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(EquipmentBelongToMapper.class)
                        .getByBelongToIdAndEquipmentId(belongTo, belongToId, equipmentId));
    }

    /**
     * 根据归属ID查询列表
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 装备列表
     */
    public static List<EquipmentGroupVO> listGroupByBelongToId(String belongTo, Long belongToId) {
        List<EquipmentGroupQueryDTO> queryDTOList = DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(EquipmentBelongToMapper.class).listGroupByBelongToId(belongTo, belongToId));
        return queryDTOList.stream().map(query -> {
            EquipmentEnum equipmentEnum = EquipmentEnum.getById(query.getEquipmentId());
            EquipmentGroupVO vo = new EquipmentGroupVO();
            vo.setEquipmentName(equipmentEnum.getName());
            vo.setCount(query.getEquipmentCount());
            vo.setDesc(equipmentEnum.getDesc());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据归属ID查询是否已穿的装备
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @param equip      是否已装备
     * @return 装备列表
     */
    public static List<EquipmentBelongTo> getBelongToIdEquip(String belongTo, Long belongToId, boolean equip) {
        int equipNum = equip ? 1 : 0;
        return DatabaseProvider.getInstance().executeReturn(sqlSession ->
                sqlSession.getMapper(EquipmentBelongToMapper.class).getBelongToIdEquip(belongTo, belongToId, equipNum));
    }

    /**
     * 新增所有权
     *
     * @param equipmentBelongTo 装备归属
     */
    public static void addBelongTo(EquipmentBelongTo equipmentBelongTo) {
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(EquipmentBelongToMapper.class)
                .addBelongTo(equipmentBelongTo));
    }

    /**
     * 穿上/卸下装备
     *
     * @param id ID
     */
    public static void updateEquipment(Long id, boolean equip) {
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
    public static void updateBelongToId(Long id, String belongTo, Long belongToId) {
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(EquipmentBelongToMapper.class)
                .updateBelongToId(id, belongTo, belongToId));
    }

    /**
     * 删除所有权
     *
     * @param id ID
     */
    public static void deleteById(Long id) {
        DatabaseProvider.getInstance().execute(sqlSession -> sqlSession.getMapper(EquipmentBelongToMapper.class)
                .deleteById(id));
    }
}
