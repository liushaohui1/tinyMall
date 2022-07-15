package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.service.IApCouponService;
import com.aprilz.tiny.service.IApCouponUserService;
import com.aprilz.tiny.vo.CouponVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 优惠券信息及规则表 前端控制器
 * </p>
 *
 * @author aprilz
 * @since 2022-07-14
 */
@RestController
@RequestMapping("/coupon")
public class ApCouponController {

    @Autowired
    private IApCouponService couponService;
    @Autowired
    private IApCouponUserService couponUserService;

    @GetMapping("mylist")
    public CommonResult mylist(@RequestParam("status") Short status,
                               @RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "size", defaultValue = "10") Integer size,
                               @RequestParam(defaultValue = "add_time") String sort,
                               @RequestParam(defaultValue = "desc") String order) {
        IPage<CouponVo> coupons = couponService.queryList(null, status, page, size, sort, order);
        return CommonResult.success(coupons);

    }


}
