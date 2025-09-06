package com.example.ybook.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.common.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ybook.dto.UserCreateDTO;
import com.example.ybook.dto.UserUpdateDTO;
import com.example.ybook.entity.UserEntity;
import com.example.ybook.vo.UserVO;

import java.util.List;

/**
 * <p>
 * 用户服务接口
 * </p>
 *
 * @author 柒
 * @since 2025-09-03 21:57:34
 */
public interface UserService extends IService<UserEntity> {
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户视图对象
     */
    UserVO getUserById(Long id);
    
    /**
     * 查询所有用户
     * @return 用户视图对象列表
     */
    List<UserVO> listAllUsers();
    
    /**
     * 分页查询用户
     * @param page 分页参数
     * @return 分页用户视图对象列表
     */
    PageResult<UserVO> pageUsers(Page<UserEntity> page);
    
    /**
     * 创建用户
     * @param userCreateDTO 用户创建DTO
     * @return 创建后的用户VO
     */
    UserVO createUser(UserCreateDTO userCreateDTO);
    
    /**
     * 更新用户
     * @param id 用户ID
     * @param userUpdateDTO 用户更新DTO
     * @return 更新后的用户VO
     */
    UserVO updateUser(Long id, UserUpdateDTO userUpdateDTO);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long id);
}
