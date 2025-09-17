package com.example.ybook.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ybook.common.ApiCode;
import com.example.ybook.common.PageResult;
import com.example.ybook.exception.BizException;
import com.example.ybook.dto.UserCreateDTO;
import com.example.ybook.dto.UserUpdateDTO;
import com.example.ybook.entity.UserEntity;
import com.example.ybook.mapper.UserMapper;
import com.example.ybook.converter.UserConverter;
import com.example.ybook.service.ChatRoomService;
import com.example.ybook.service.UserService;
import com.example.ybook.mapper.UserFollowMapper;
import com.example.ybook.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户服务实现类
 * </p>
 *
 * @author 柒
 * @since 2025-09-03 21:57:34
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserConverter userConverter;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Override
    public UserVO getUserById(Long id) {
        UserEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ApiCode.USER_NOT_FOUND);
        }
        return userConverter.entityToVO(entity);
    }

    @Override
    public List<UserVO> listAllUsers() {
        List<UserEntity> entities = this.list();
        return entities.stream()
                .map(userConverter::entityToVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<UserVO> pageUsers(Page<UserEntity> page) {
        Page<UserEntity> entityPage = this.page(page);
        List<UserVO> voList = entityPage.getRecords().stream()
                .map(userConverter::entityToVO)
                .collect(Collectors.toList());
        return PageResult.<UserVO>builder()
                .current(entityPage.getCurrent())
                .size(entityPage.getSize())
                .total(entityPage.getTotal())
                .pages(entityPage.getPages())
                .records(voList)
                .build();
    }

    @Override
    @Transactional
    public UserVO createUser(UserCreateDTO userCreateDTO) {
        // 检查用户名是否已存在
        if (this.lambdaQuery().eq(UserEntity::getUsername, userCreateDTO.getUsername()).exists()) {
            throw new BizException(ApiCode.USERNAME_EXISTS);
        }

        // 检查邮箱是否已存在
        if (this.lambdaQuery().eq(UserEntity::getEmail, userCreateDTO.getEmail()).exists()) {
            throw new BizException(ApiCode.EMAIL_EXISTS);
        }

        // 转换DTO到实体
        UserEntity entity = userConverter.createDTOToEntity(userCreateDTO);

        // 加密密码
        entity.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));

        // 保存用户
        this.save(entity);
        return userConverter.entityToVO(entity);
    }

    @Override
    @Transactional
    public UserVO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        // 获取现有用户
        UserEntity existingEntity = this.getById(id);
        if (existingEntity == null) {
            throw new BizException(ApiCode.USER_NOT_FOUND);
        }

        // 如果要更新用户名，检查是否与其他用户重复
        if (userUpdateDTO.getUsername() != null &&
                !userUpdateDTO.getUsername().equals(existingEntity.getUsername())) {
            if (this.lambdaQuery()
                    .eq(UserEntity::getUsername, userUpdateDTO.getUsername())
                    .ne(UserEntity::getId, id)
                    .exists()) {
                throw new BizException(ApiCode.USERNAME_EXISTS);
            }
        }

        // 如果要更新邮箱，检查是否与其他用户重复
        if (userUpdateDTO.getEmail() != null &&
                !userUpdateDTO.getEmail().equals(existingEntity.getEmail())) {
            if (this.lambdaQuery()
                    .eq(UserEntity::getEmail, userUpdateDTO.getEmail())
                    .ne(UserEntity::getId, id)
                    .exists()) {
                throw new BizException(ApiCode.EMAIL_EXISTS);
            }
        }

        // 更新实体 (注意：MapStruct 不会更新密码、状态、ID、时间字段)
        UserEntity entity = userConverter.updateDTOToEntity(id, userUpdateDTO);

        // 保存更新
        this.updateById(entity);
        return userConverter.entityToVO(this.getById(id));
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        // 检查用户是否存在
        UserEntity user = this.getById(id);
        if (user == null) {
            throw new BizException(ApiCode.USER_NOT_FOUND);
        }

        // 1. 删除用户相关的聊天数据（级联删除）
        chatRoomService.deleteUserChatData(id);

        // 2. 删除用户的关注关系数据
        userFollowMapper.deleteByUserId(id);

        // 3. 这里可以添加其他相关数据的删除逻辑
        // 例如：删除用户的笔记、评论等

        // 4. 最后删除用户本身
        return this.removeById(id);
    }
}
