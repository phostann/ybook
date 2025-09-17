package com.example.ybook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.entity.UserFollowEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户关注 Mapper 接口
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollowEntity> {
    
    /**
     * 根据关注者ID和被关注者ID查询关注关系
     */
    UserFollowEntity selectByFollowerIdAndFollowingId(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    
    /**
     * 批量查询用户对多个用户的关注状态
     * @param followerId 关注者ID
     * @param followingIds 被关注者ID列表
     * @return 关注关系列表
     */
    List<UserFollowEntity> selectBatchByFollowerIdAndFollowingIds(@Param("followerId") Long followerId, @Param("followingIds") List<Long> followingIds);
    
    /**
     * 分页查询用户的关注列表（我关注的人）
     */
    Page<Long> selectFollowingIdsByFollowerId(Page<Long> page, @Param("followerId") Long followerId);
    
    /**
     * 分页查询用户的粉丝列表（关注我的人）
     */
    Page<Long> selectFollowerIdsByFollowingId(Page<Long> page, @Param("followingId") Long followingId);
    
    /**
     * 统计用户的关注数（我关注的人数）
     */
    Long countFollowingByFollowerId(@Param("followerId") Long followerId);
    
    /**
     * 统计用户的粉丝数（关注我的人数）
     */
    Long countFollowersByFollowingId(@Param("followingId") Long followingId);
    
    /**
     * 根据用户ID删除所有相关的关注记录（清理用户数据时使用）
     * @param userId 用户ID
     * @return 删除的记录数
     */
    int deleteByUserId(@Param("userId") Long userId);
}