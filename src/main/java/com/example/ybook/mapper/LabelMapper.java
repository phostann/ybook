package com.example.ybook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ybook.entity.LabelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 标签 Mapper
 * </p>
 */
@Mapper
public interface LabelMapper extends BaseMapper<LabelEntity> {
    
    /**
     * 根据标签名称列表批量查询
     */
    List<LabelEntity> selectByNames(@Param("names") List<String> names);
}
