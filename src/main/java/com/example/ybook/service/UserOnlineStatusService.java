package com.example.ybook.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ybook.entity.UserOnlineStatusEntity;

import java.util.List;

public interface UserOnlineStatusService extends IService<UserOnlineStatusEntity> {

    /**
     * 更新用户在线状态
     * @param userId 用户ID
     * @param status 状态
     * @param deviceType 设备类型
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    void updateOnlineStatus(Long userId, String status, String deviceType, String ipAddress, String userAgent);

    /**
     * 获取聊天室成员在线状态
     * @param roomId 聊天室ID
     * @return 在线状态列表
     */
    List<UserOnlineStatusEntity> getRoomMembersOnlineStatus(Long roomId);

    /**
     * 用户上线
     * @param userId 用户ID
     * @param deviceType 设备类型
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    void userOnline(Long userId, String deviceType, String ipAddress, String userAgent);

    /**
     * 用户离线
     * @param userId 用户ID
     */
    void userOffline(Long userId);
}