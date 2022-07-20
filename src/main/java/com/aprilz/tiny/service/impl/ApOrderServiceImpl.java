package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApOrderMapper;
import com.aprilz.tiny.mbg.entity.ApOrder;
import com.aprilz.tiny.service.IApOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApOrderServiceImpl extends ServiceImpl<ApOrderMapper, ApOrder> implements IApOrderService {

}
