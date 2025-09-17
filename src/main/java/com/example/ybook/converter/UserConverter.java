package com.example.ybook.converter;

import com.example.ybook.dto.UserCreateDTO;
import com.example.ybook.dto.UserUpdateDTO;
import com.example.ybook.entity.UserEntity;
import com.example.ybook.vo.UserVO;
import org.mapstruct.*;

/**
 * <p>
 * 用户实体映射器
 * </p>
 *
 * @author 柒
 * @since 2025-09-05
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * UserEntity 转换为 UserVO
     */
    @Mapping(target = "followingCount", ignore = true)
    @Mapping(target = "followerCount", ignore = true)
    @Mapping(target = "isFollowing", ignore = true)
    UserVO entityToVO(UserEntity entity);
    
    /**
     * UserEntity 转换为 UserVO（简化版本，忽略额外字段）
     */
    @Mapping(target = "followingCount", ignore = true)
    @Mapping(target = "followerCount", ignore = true)
    @Mapping(target = "isFollowing", ignore = true)
    UserVO toVO(UserEntity entity);

    /**
     * UserCreateDTO 转换为 UserEntity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "status", constant = "1")
    UserEntity createDTOToEntity(UserCreateDTO dto);

    /**
     * UserUpdateDTO 转换为 UserEntity（仅映射非空字段），并带上主键 id 用于更新
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    UserEntity updateDTOToEntity(Long id, UserUpdateDTO dto);
}
