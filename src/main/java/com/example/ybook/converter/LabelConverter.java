package com.example.ybook.converter;

import com.example.ybook.dto.LabelCreateDTO;
import com.example.ybook.dto.LabelUpdateDTO;
import com.example.ybook.entity.LabelEntity;
import com.example.ybook.vo.LabelVO;
import org.mapstruct.*;

/**
 * 标签实体映射器
 */
@Mapper(componentModel = "spring")
public interface LabelConverter {

    LabelVO entityToVO(LabelEntity entity);

    LabelEntity createDTOToEntity(LabelCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", source = "id")
    LabelEntity updateDTOToEntity(Long id, LabelUpdateDTO dto);
}
