package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApSearchHistoryMapper;
import com.aprilz.tiny.mbg.entity.ApSearchHistory;
import com.aprilz.tiny.service.IApSearchHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 搜索历史表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApSearchHistoryServiceImpl extends ServiceImpl<ApSearchHistoryMapper, ApSearchHistory> implements IApSearchHistoryService {

    @Override
    @Transactional
    public Integer deleteByUid(Long id) {

        this.lambdaUpdate().set(ApSearchHistory::getDeleteFlag, false).eq(ApSearchHistory::getUserId, id)
                .update();
    }
}
