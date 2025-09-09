package com.example.ybook.converter;

import com.example.ybook.dto.NoteCreateDTO;
import com.example.ybook.dto.NoteUpdateDTO;
import com.example.ybook.entity.LabelEntity;
import com.example.ybook.entity.NoteEntity;
import com.example.ybook.vo.LabelVO;
import com.example.ybook.vo.NoteVO;
import org.mapstruct.*;

import java.util.List;

/**
 * 笔记实体映射器
 */
@Mapper(componentModel = "spring", uses = { LabelConverter.class })
public interface NoteConverter {

    /**
     * 实体转换为VO
     */
    @Mapping(target = "labels", source = "labels")
    NoteVO entityToVO(NoteEntity entity, List<LabelEntity> labels);

    /**
     * 实体转换为VO（不包含标签）
     */
    @Mapping(target = "labels", ignore = true)
    NoteVO entityToVO(NoteEntity entity);

    /**
     * 创建DTO转换为实体
     */
    @Mapping(target = "id", ignore = true) 
    NoteEntity createDTOToEntity(Long uid, NoteCreateDTO dto);

    /**
     * 更新DTO转换为实体
     */
    NoteEntity updateDTOToEntity(Long id, NoteUpdateDTO dto);

    /**
     * 标签实体列表转换为VO列表
     */
    List<LabelVO> labelEntitiesToVOs(List<LabelEntity> labels);
}