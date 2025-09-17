package com.example.ybook.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.vo.FollowStatusVO;
import com.example.ybook.vo.FollowStatsVO;
import com.example.ybook.vo.UserVO;

import java.util.List;

/**
 * 用户关注服务接口
 */
public interface UserFollowService {
    
    /**
     * 切换关注状态
     * @param followingId 被关注用户ID
     * @return 是否关注成功（true-已关注，false-已取消关注）
     */
    boolean toggleFollow(Long followingId);
    
    /**
     * 获取用户对单个用户的关注状态
     * @param followingId 被关注用户ID
     * @param followerId 关注者ID，如果为null则使用当前登录用户
     * @return 关注状态
     */
    boolean getFollowStatus(Long followingId, Long followerId);
    
    /**
     * 批量获取用户对多个用户的关注状态
     * @param followingIds 被关注用户ID列表
     * @param followerId 关注者ID，如果为null则使用当前登录用户
     * @return 关注状态列表
     */
    List<FollowStatusVO> batchGetFollowStatus(List<Long> followingIds, Long followerId);
    
    /**
     * 获取用户的关注列表（我关注的人）
     * @param followerId 关注者ID，如果为null则使用当前登录用户
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 关注的用户列表
     */
    Page<UserVO> getUserFollowing(Long followerId, int pageNum, int pageSize);
    
    /**
     * 获取用户的粉丝列表（关注我的人）
     * @param followingId 被关注者ID，如果为null则使用当前登录用户
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 粉丝用户列表
     */
    Page<UserVO> getUserFollowers(Long followingId, int pageNum, int pageSize);
    
    /**
     * 获取用户关注统计
     * @param userId 用户ID
     * @return 关注统计信息
     */
    FollowStatsVO getFollowStats(Long userId);
    
    /**
     * 异步更新用户的关注统计
     * @param userId 用户ID
     */
    void updateFollowStatsAsync(Long userId);
}