package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApCoupon;
import com.aprilz.tiny.vo.CouponVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
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

    IPage<CouponVo> queryList(Integer couponId, Short status, Integer page, Integer limit, String sort, String order);
}
