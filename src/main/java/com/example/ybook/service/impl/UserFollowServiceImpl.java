package com.example.ybook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ybook.common.ApiCode;
import com.example.ybook.converter.UserConverter;
import com.example.ybook.entity.UserEntity;
import com.example.ybook.entity.UserFollowEntity;
import com.example.ybook.exception.BizException;
import com.example.ybook.mapper.UserFollowMapper;
import com.example.ybook.mapper.UserMapper;
import com.example.ybook.security.CurrentUserContext;
import com.example.ybook.service.UserFollowService;
import com.example.ybook.vo.FollowStatusVO;
import com.example.ybook.vo.FollowStatsVO;
import com.example.ybook.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户关注服务实现
 */
@Slf4j
@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollowEntity>
        implements UserFollowService {

    private final UserMapper userMapper;
    private final UserConverter userConverter;

    public UserFollowServiceImpl(UserMapper userMapper, UserConverter userConverter) {
        this.userMapper = userMapper;
        this.userConverter = userConverter;
    }

    @Override
    @Transactional
    public boolean toggleFollow(Long followingId) {
        Long currentUserId = CurrentUserContext.getUserId();
        
        // 防止自己关注自己
        if (currentUserId.equals(followingId)) {
            throw new BizException(ApiCode.PARAM_INVALID, "不能关注自己");
        }
        
        // 检查被关注用户是否存在
        UserEntity followingUser = userMapper.selectById(followingId);
        if (followingUser == null) {
            throw new BizException(ApiCode.NOT_FOUND, "用户不存在");
        }
        
        // 查询现有关注关系
        UserFollowEntity existingFollow = baseMapper.selectByFollowerIdAndFollowingId(currentUserId, followingId);
        
        if (existingFollow == null) {
            // 创建新的关注关系
            UserFollowEntity newFollow = new UserFollowEntity();
            newFollow.setFollowerId(currentUserId);
            newFollow.setFollowingId(followingId);
            newFollow.setFollowing(true);
            baseMapper.insert(newFollow);
            
            // 异步更新统计
            updateFollowStatsAsync(currentUserId);
            updateFollowStatsAsync(followingId);
            
            log.info("用户 {} 关注了用户 {}", currentUserId, followingId);
            return true;
        } else {
            // 切换关注状态
            boolean newStatus = !existingFollow.isFollowing();
            existingFollow.setFollowing(newStatus);
            baseMapper.updateById(existingFollow);
            
            // 异步更新统计
            updateFollowStatsAsync(currentUserId);
            updateFollowStatsAsync(followingId);
            
            log.info("用户 {} {} 用户 {}", currentUserId, newStatus ? "关注了" : "取消关注了", followingId);
            return newStatus;
        }
    }

    @Override
    public boolean getFollowStatus(Long followingId, Long followerId) {
        if (followerId == null) {
            followerId = CurrentUserContext.getUserId();
        }
        
        UserFollowEntity followRelation = baseMapper.selectByFollowerIdAndFollowingId(followerId, followingId);
        return followRelation != null && followRelation.isFollowing();
    }

    @Override
    public List<FollowStatusVO> batchGetFollowStatus(List<Long> followingIds, Long followerId) {
        if (followerId == null) {
            followerId = CurrentUserContext.getUserId();
        }
        
        if (followingIds == null || followingIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 批量查询关注关系
        List<UserFollowEntity> followRelations = baseMapper.selectBatchByFollowerIdAndFollowingIds(followerId, followingIds);
        
        // 转换为Map方便查找
        Map<Long, UserFollowEntity> followMap = followRelations.stream()
            .collect(Collectors.toMap(UserFollowEntity::getFollowingId, Function.identity()));
        
        // 构建结果列表
        return followingIds.stream()
            .map(followingId -> {
                FollowStatusVO statusVO = new FollowStatusVO();
                statusVO.setUserId(followingId);
                UserFollowEntity followRelation = followMap.get(followingId);
                statusVO.setIsFollowing(followRelation != null && followRelation.isFollowing());
                return statusVO;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Page<UserVO> getUserFollowing(Long followerId, int pageNum, int pageSize) {
        if (followerId == null) {
            followerId = CurrentUserContext.getUserId();
        }
        
        Page<Long> userIdPage = new Page<>(pageNum, pageSize);
        Page<Long> followingUserIds = baseMapper.selectFollowingIdsByFollowerId(userIdPage, followerId);
        
        if (followingUserIds.getRecords().isEmpty()) {
            return new Page<>(pageNum, pageSize, 0);
        }
        
        // 批量查询用户信息
        List<UserEntity> users = userMapper.selectBatchIds(followingUserIds.getRecords());
        List<UserVO> userVOs = users.stream()
            .map(userConverter::toVO)
            .collect(Collectors.toList());
        
        Page<UserVO> result = new Page<>(pageNum, pageSize, followingUserIds.getTotal());
        result.setRecords(userVOs);
        return result;
    }

    @Override
    public Page<UserVO> getUserFollowers(Long followingId, int pageNum, int pageSize) {
        if (followingId == null) {
            followingId = CurrentUserContext.getUserId();
        }
        
        Page<Long> userIdPage = new Page<>(pageNum, pageSize);
        Page<Long> followerUserIds = baseMapper.selectFollowerIdsByFollowingId(userIdPage, followingId);
        
        if (followerUserIds.getRecords().isEmpty()) {
            return new Page<>(pageNum, pageSize, 0);
        }
        
        // 批量查询用户信息
        List<UserEntity> users = userMapper.selectBatchIds(followerUserIds.getRecords());
        List<UserVO> userVOs = users.stream()
            .map(userConverter::toVO)
            .collect(Collectors.toList());
        
        Page<UserVO> result = new Page<>(pageNum, pageSize, followerUserIds.getTotal());
        result.setRecords(userVOs);
        return result;
    }

    @Override
    public FollowStatsVO getFollowStats(Long userId) {
        FollowStatsVO statsVO = new FollowStatsVO();
        statsVO.setUserId(userId);
        
        // 查询关注数和粉丝数
        Long followingCount = baseMapper.countFollowingByFollowerId(userId);
        Long followerCount = baseMapper.countFollowersByFollowingId(userId);
        
        statsVO.setFollowingCount(followingCount != null ? followingCount : 0L);
        statsVO.setFollowerCount(followerCount != null ? followerCount : 0L);
        
        return statsVO;
    }

    @Override
    @Async
    public void updateFollowStatsAsync(Long userId) {
        try {
            // 这里可以实现缓存更新逻辑
            FollowStatsVO stats = getFollowStats(userId);
            log.debug("更新用户 {} 关注统计: 关注数={}, 粉丝数={}", 
                userId, stats.getFollowingCount(), stats.getFollowerCount());
        } catch (Exception e) {
            log.error("异步更新用户 {} 关注统计失败", userId, e);
        }
    }
}