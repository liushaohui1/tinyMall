package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApCoupon;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 优惠券信息及规则表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-14
 */
public interface IApCouponService extends IService<ApCoupon> {

    void assignForRegister(Long id);
}
