package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApBrandMapper;
import com.aprilz.tiny.mbg.entity.ApBrand;
import com.aprilz.tiny.service.IApBrandService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 品牌商表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApBrandServiceImpl extends ServiceImpl<ApBrandMapper, ApBrand> implements IApBrandService {

    @Override
    public List<ApBrand> query(Integer offset, Integer limit) {
        LambdaQueryWrapper<ApBrand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApBrand::getDeleteFlag, true)
                .orderByDesc(ApBrand::getCreateTime).last("limit " + offset + "," + limit);
        return this.list(queryWrapper);
    }
}
