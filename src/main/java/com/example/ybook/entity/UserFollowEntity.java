package com.example.ybook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户关注关系实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("y_user_follow")
public class UserFollowEntity extends BaseEntity {
    
    /**
     * 关注者ID（发起关注的用户）
     */
    private Long followerId;
    
    /**
     * 被关注者ID（被关注的用户）
     */
    private Long followingId;
    
    /**
     * 关注状态: 1-关注 0-取消关注
     */
    private Integer status;
    
    /**
     * 检查是否正在关注
     */
    public boolean isFollowing() {
        return status != null && status == 1;
    }
    
    /**
     * 设置关注状态
     */
    public void setFollowing(boolean following) {
        this.status = following ? 1 : 0;
    }
}