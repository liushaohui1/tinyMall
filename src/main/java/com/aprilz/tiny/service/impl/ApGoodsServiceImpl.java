package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApGoodsMapper;
import com.aprilz.tiny.mbg.entity.ApGoods;
import com.aprilz.tiny.service.IApGoodsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品基本信息表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApGoodsServiceImpl extends ServiceImpl<ApGoodsMapper, ApGoods> implements IApGoodsService {

    @Override
    public List<ApGoods> queryByNew(Integer offset, Integer limit) {
        LambdaQueryWrapper<ApGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApGoods::getIsNew, true).eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, true)
                .orderByDesc(ApGoods::getCreateTime).last("limit " + offset + "," + limit);
        return this.list(queryWrapper);
    }

    @Override
    public List<ApGoods> queryByHot(Integer offset, Integer limit) {
        LambdaQueryWrapper<ApGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApGoods::getIsHot, true).eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, true)
                .orderByDesc(ApGoods::getCreateTime).last("limit " + offset + "," + limit);
        return this.list(queryWrapper);
    }
}
