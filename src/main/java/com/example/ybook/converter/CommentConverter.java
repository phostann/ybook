package com.example.ybook.converter;

import com.example.ybook.dto.CommentCreateDTO;
import com.example.ybook.dto.CommentUpdateDTO;
import com.example.ybook.entity.CommentEntity;
import com.example.ybook.vo.CommentVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 评论实体映射器（支持多层嵌套回复）
 */
@Mapper(componentModel = "spring")
public interface CommentConverter {

    /**
     * 实体转换为VO
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "isLiked", ignore = true)
    @Mapping(target = "replyToUser", ignore = true)
    @Mapping(target = "replyToContent", ignore = true)
    CommentVO toVO(CommentEntity entity);

    /**
     * 创建DTO转换为实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "commentLevel", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "replyCount", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    CommentEntity toEntity(CommentCreateDTO dto);

    /**
     * 更新DTO转换为实体（用于部分更新）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "noteId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "rootCommentId", ignore = true)
    @Mapping(target = "replyToCommentId", ignore = true)
    @Mapping(target = "commentLevel", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "replyCount", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "ipLocation", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    CommentEntity toEntity(CommentUpdateDTO dto);
}