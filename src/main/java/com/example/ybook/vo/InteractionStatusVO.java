package com.example.ybook.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 用户对笔记的交互状态 VO
 */
@Data
@Schema(name = "InteractionStatusVO", description = "用户对笔记的交互状态视图对象")
public class InteractionStatusVO {
    
    /**
     * 笔记ID -> 交互状态的映射
     * key: 笔记ID
     * value: 交互详情
     */
    @Schema(description = "笔记ID与交互状态的映射关系")
    private Map<Long, InteractionDetailVO> interactions;
    
    @Data
    @Schema(name = "InteractionDetailVO", description = "单个笔记的交互详情")
    public static class InteractionDetailVO {
        /**
         * 是否已点赞
         */
        @Schema(description = "是否已点赞", example = "true")
        private Boolean isLiked;
        
        /**
         * 是否已收藏
         */
        @Schema(description = "是否已收藏", example = "false")
        private Boolean isFavorited;
        
        /**
         * 是否已分享
         */
        @Schema(description = "是否已分享", example = "false")
        private Boolean isShared;
    }
}