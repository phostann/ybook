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
@Mapper(componentModel = "spring", uses = {LabelConverter.class})
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
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "viewCount", constant = "0")
    @Mapping(target = "likeCount", constant = "0")
    @Mapping(target = "commentCount", constant = "0")
    @Mapping(target = "collectCount", constant = "0")
    NoteEntity createDTOToEntity(NoteCreateDTO dto);
    
    /**
     * 更新DTO转换为实体
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "collectCount", ignore = true)
    NoteEntity updateDTOToEntity(Long id, NoteUpdateDTO dto);
    
    /**
     * 标签实体列表转换为VO列表
     */
    List<LabelVO> labelEntitiesToVOs(List<LabelEntity> labels);
}