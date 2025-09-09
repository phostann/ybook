package com.example.ybook.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.LabelCreateDTO;
import com.example.ybook.dto.LabelUpdateDTO;
import com.example.ybook.entity.LabelEntity;
import com.example.ybook.vo.LabelVO;

import java.util.List;

/**
 * 标签服务接口
 */
public interface LabelService extends IService<LabelEntity> {
    LabelVO getByLabelId(Long id);
    List<LabelVO> listAll();
    List<LabelVO> listAll(String name);
    PageResult<LabelVO> pageLabels(Page<LabelEntity> page);
    LabelVO createLabel(LabelCreateDTO dto);
    LabelVO updateLabel(Long id, LabelUpdateDTO dto);
    boolean deleteLabel(Long id);
    
    /**
     * 更新标签使用次数
     */
    void updateUseCount(Long labelId, int delta);
    
    /**
     * 重新计算标签使用次数（基于实际关联数据）
     */
    void recalculateUseCount(Long labelId);
    
    /**
     * 批量更新标签使用次数
     */
    void batchUpdateUseCount(List<Long> labelIds, int delta);
}

