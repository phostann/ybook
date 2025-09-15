package com.example.ybook.common;

/**
 * 用户笔记交互类型枚举
 */
public enum InteractionType {
    
    LIKE(1, "点赞"),
    FAVORITE(2, "收藏"), 
    SHARE(4, "分享"),
    REPORT(8, "举报");
    
    private final int value;
    private final String description;
    
    InteractionType(int value, String description) {
        this.value = value;
        this.description = description;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查交互类型是否包含指定类型
     */
    public static boolean hasType(int interactionType, InteractionType type) {
        return (interactionType & type.getValue()) > 0;
    }
    
    /**
     * 添加交互类型
     */
    public static int addType(int interactionType, InteractionType type) {
        return interactionType | type.getValue();
    }
    
    /**
     * 移除交互类型
     */
    public static int removeType(int interactionType, InteractionType type) {
        return interactionType & ~type.getValue();
    }
    
    /**
     * 切换交互类型
     */
    public static int toggleType(int interactionType, InteractionType type) {
        if (hasType(interactionType, type)) {
            return removeType(interactionType, type);
        } else {
            return addType(interactionType, type);
        }
    }
}