package com.example.ybook.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ybook.common.ApiCode;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.LabelCreateDTO;
import com.example.ybook.dto.LabelUpdateDTO;
import com.example.ybook.entity.LabelEntity;
import com.example.ybook.exception.BizException;
import com.example.ybook.mapper.NoteLabelMapper;
import com.example.ybook.mapper.LabelMapper;
import com.example.ybook.converter.LabelConverter;
import com.example.ybook.service.LabelService;
import com.example.ybook.vo.LabelVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现
 */
@Service
public class LabelServiceImpl extends ServiceImpl<LabelMapper, LabelEntity> implements LabelService {

    private final LabelConverter labelConverter;
    private final NoteLabelMapper noteLabelMapper;

    public LabelServiceImpl(LabelConverter labelConverter, NoteLabelMapper noteLabelMapper) {
        this.labelConverter = labelConverter;
        this.noteLabelMapper = noteLabelMapper;
    }

    @Override
    public LabelVO getByLabelId(Long id) {
        LabelEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ApiCode.LABEL_NOT_FOUND);
        }
        return labelConverter.entityToVO(entity);
    }

    @Override
    public List<LabelVO> listAll() {
        return this.list().stream()
                .map(labelConverter::entityToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabelVO> listAll(String name) {
        if (name == null || name.trim().isEmpty()) {
            return listAll();
        }
        return this.lambdaQuery()
                .like(LabelEntity::getName, name.trim())
                .list()
                .stream()
                .map(labelConverter::entityToVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<LabelVO> pageLabels(Page<LabelEntity> page) {
        Page<LabelEntity> entityPage = this.page(page);
        List<LabelVO> voList = entityPage.getRecords().stream()
                .map(labelConverter::entityToVO)
                .collect(Collectors.toList());
        return PageResult.<LabelVO>builder()
                .current(entityPage.getCurrent())
                .size(entityPage.getSize())
                .total(entityPage.getTotal())
                .pages(entityPage.getPages())
                .records(voList)
                .build();
    }

    @Override
    @Transactional
    public LabelVO createLabel(LabelCreateDTO dto) {
        // 名称唯一性校验（DB 未加唯一键，应用层兜底）
        boolean exists = this.lambdaQuery()
                .eq(LabelEntity::getName, dto.getName())
                .exists();
        if (exists) {
            throw new BizException(ApiCode.LABEL_NAME_EXISTS);
        }
        LabelEntity entity = labelConverter.createDTOToEntity(dto);
        // 初始使用次数为 0
        entity.setUseCount(0);
        this.save(entity);
        return labelConverter.entityToVO(entity);
    }

    @Override
    @Transactional
    public LabelVO updateLabel(Long id, LabelUpdateDTO dto) {
        LabelEntity existing = this.getById(id);
        if (existing == null) {
            throw new BizException(ApiCode.LABEL_NOT_FOUND);
        }
        if (dto.getName() != null && !dto.getName().equals(existing.getName())) {
            boolean dup = this.lambdaQuery()
                    .eq(LabelEntity::getName, dto.getName())
                    .ne(LabelEntity::getId, id)
                    .exists();
            if (dup) {
                throw new BizException(ApiCode.LABEL_NAME_EXISTS);
            }
        }
        LabelEntity toUpdate = labelConverter.updateDTOToEntity(id, dto);
        // 不允许直接改 useCount（统计项）
        toUpdate.setUseCount(null);
        this.updateById(toUpdate);
        return labelConverter.entityToVO(this.getById(id));
    }

    @Override
    public boolean deleteLabel(Long id) {
        return this.removeById(id);
    }
    
    @Override
    public void updateUseCount(Long labelId, int delta) {
        LabelEntity label = this.getById(labelId);
        if (label != null) {
            int newCount = Math.max(0, (label.getUseCount() != null ? label.getUseCount() : 0) + delta);
            label.setUseCount(newCount);
            this.updateById(label);
        }
    }
    
    @Override
    public void recalculateUseCount(Long labelId) {
        LabelEntity label = this.getById(labelId);
        if (label != null) {
            int actualCount = noteLabelMapper.countUsageByLabelId(labelId);
            label.setUseCount(actualCount);
            this.updateById(label);
        }
    }
    
    @Override
    public void batchUpdateUseCount(List<Long> labelIds, int delta) {
        for (Long labelId : labelIds) {
            updateUseCount(labelId, delta);
        }
    }
}
