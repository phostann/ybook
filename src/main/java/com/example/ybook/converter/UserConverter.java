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
    UserVO entityToVO(UserEntity entity);

    /**
     * UserCreateDTO 转换为 UserEntity
     */
    @Mapping(target = "status", constant = "1")
    UserEntity createDTOToEntity(UserCreateDTO dto);

    /**
     * UserUpdateDTO 转换为 UserEntity（仅映射非空字段），并带上主键 id 用于更新
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", source = "id")
    UserEntity updateDTOToEntity(Long id, UserUpdateDTO dto);
}
