package com.example.ybook.converter;

import com.example.ybook.entity.UserNoteInteractionEntity;
import com.example.ybook.vo.InteractionStatusVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户笔记交互转换器
 */
@Mapper(componentModel = "spring")
public interface UserNoteInteractionConverter {
    
    /**
     * 实体转换为交互详情VO
     */
    @Mapping(target = "isLiked", expression = "java(entity.isLiked())")
    @Mapping(target = "isFavorited", expression = "java(entity.isFavorited())")
    @Mapping(target = "isShared", expression = "java(entity.isShared())")
    InteractionStatusVO.InteractionDetailVO toInteractionDetailVO(UserNoteInteractionEntity entity);
}