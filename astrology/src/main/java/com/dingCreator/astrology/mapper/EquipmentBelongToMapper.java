package com.dingCreator.astrology.mapper;

import com.dingCreator.astrology.dto.equipment.EquipmentGroupQueryDTO;
import com.dingCreator.astrology.entity.EquipmentBelongTo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author ding
 * @date 2024/4/10
 */
@Mapper
public interface EquipmentBelongToMapper {

    /**
     * 根据ID查询信息
     *
     * @param id ID
     * @return 信息
     */
    @Select("select * from astrology_equipment_belong_to where id=#{id}")
    EquipmentBelongTo getById(@Param("id") Long id);

    /**
     * 根据归属ID查询列表
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 装备列表
     */
    @Select("select * from astrology_equipment_belong_to where belong_to=#{belongTo} and belong_to_id=#{belongToId}")
    List<EquipmentBelongTo> listByBelongToId(@Param("belongTo") String belongTo, @Param("belongToId") Long belongToId);

    /**
     * 根据归属ID和装备ID查询列表
     *
     * @param belongTo    归属
     * @param belongToId  归属ID
     * @param equipmentId 装备ID
     * @return 装备列表
     */
    @Select("select * from astrology_equipment_belong_to where belong_to=#{belongTo} and belong_to_id=#{belongToId} " +
            "and equipment_id=#{equipmentId}")
    List<EquipmentBelongTo> getByBelongToIdAndEquipmentId(@Param("belongTo") String belongTo,
                                                          @Param("belongToId") Long belongToId,
                                                          @Param("equipmentId") Long equipmentId);

    /**
     * 根据归属ID查询组
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 装备列表
     */
    @Select("select equipment_id,count(1) equipmentCount from astrology_equipment_belong_to where belong_to=#{belongTo} " +
            "and belong_to_id=#{belongToId} group by equipment_id")
    List<EquipmentGroupQueryDTO> listGroupByBelongToId(@Param("belongTo") String belongTo,
                                                       @Param("belongToId") Long belongToId);

    /**
     * 根据归属ID查询是否为已穿的装备
     *
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @param equip      是否已装备
     * @return 装备列表
     */
    @Select("select * from astrology_equipment_belong_to where belong_to=#{belongTo} and belong_to_id=#{belongToId} " +
            "and equip=#{equip}")
    List<EquipmentBelongTo> getBelongToIdEquip(@Param("belongTo") String belongTo, @Param("belongToId") Long belongToId,
                                               @Param("equip") Integer equip);

    /**
     * 新增所有权
     *
     * @param equipmentBelongTo 装备归属
     */
    @Insert("INSERT INTO astrology_equipment_belong_to(belong_to, belong_to_id, equipment_id, `level`, equip)" +
            "VALUES (#{belongTo}, #{belongToId}, #{equipmentId}, #{level}, #{equip})")
    void addBelongTo(EquipmentBelongTo equipmentBelongTo);

    /**
     * 所有权变更
     *
     * @param id         ID
     * @param belongTo   归属
     * @param belongToId 归属ID
     * @return 1
     */
    @Update("update astrology_equipment_belong_to set belong_to=#{belongTo},belong_to_id=#{belongToId} where id=#{id}")
    int updateBelongToId(@Param("id") Long id, @Param("belongTo") String belongTo, @Param("belongToId") Long belongToId);

    /**
     * 删除所有权
     *
     * @param id ID
     * @return 1
     */
    @Delete("delete from astrology_equipment_belong_to where id=#{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 穿上/卸下装备
     *
     * @param id    ID
     * @param equip 穿上/卸下装备
     */
    @Update("update astrology_equipment_belong_to set equip=#{equip} where id=#{id}")
    void equipEquipment(@Param("id") Long id, @Param("equip") Integer equip);
}
