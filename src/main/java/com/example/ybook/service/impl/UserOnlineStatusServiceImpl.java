package com.example.ybook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ybook.entity.UserOnlineStatusEntity;
import com.example.ybook.mapper.UserOnlineStatusMapper;
import com.example.ybook.service.UserOnlineStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserOnlineStatusServiceImpl extends ServiceImpl<UserOnlineStatusMapper, UserOnlineStatusEntity> implements UserOnlineStatusService {

    @Autowired
    private UserOnlineStatusMapper userOnlineStatusMapper;

    @Override
    @Transactional
    public void updateOnlineStatus(Long userId, String status, String deviceType, String ipAddress, String userAgent) {
        UserOnlineStatusEntity onlineStatus = getByUserId(userId);
        
        if (onlineStatus == null) {
            onlineStatus = new UserOnlineStatusEntity();
            onlineStatus.setUserId(userId);
        }
        
        onlineStatus.setStatus(status);
        onlineStatus.setLastActiveTime(LocalDateTime.now());
        onlineStatus.setDeviceType(deviceType);
        onlineStatus.setIpAddress(ipAddress);
        onlineStatus.setUserAgent(userAgent);
        
        this.saveOrUpdate(onlineStatus);
    }

    @Override
    public List<UserOnlineStatusEntity> getRoomMembersOnlineStatus(Long roomId) {
        return userOnlineStatusMapper.selectByRoomId(roomId);
    }

    @Override
    public void userOnline(Long userId, String deviceType, String ipAddress, String userAgent) {
        updateOnlineStatus(userId, "ONLINE", deviceType, ipAddress, userAgent);
    }

    @Override
    public void userOffline(Long userId) {
        updateOnlineStatus(userId, "OFFLINE", null, null, null);
    }

    private UserOnlineStatusEntity getByUserId(Long userId) {
        LambdaQueryWrapper<UserOnlineStatusEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOnlineStatusEntity::getUserId, userId);
        return this.getOne(wrapper);
    }
}