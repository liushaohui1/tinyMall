package com.aprilz.tiny.service.impl;

import cn.hutool.core.date.DateUtil;
import com.aprilz.tiny.common.consts.Const;
import com.aprilz.tiny.mapper.ApCouponMapper;
import com.aprilz.tiny.mbg.entity.ApCoupon;
import com.aprilz.tiny.mbg.entity.ApCouponUser;
import com.aprilz.tiny.service.IApCouponService;
import com.aprilz.tiny.service.IApCouponUserService;
import com.aprilz.tiny.vo.CouponVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 优惠券信息及规则表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-14
 */
@Service
public class ApCouponServiceImpl extends ServiceImpl<ApCouponMapper, ApCoupon> implements IApCouponService {

    @Autowired
    private IApCouponService couponService;


    @Resource
    private IApCouponUserService couponUserService;

    @Override
    public void assignForRegister(Long userId) {
        //查询注册可领用的优惠券
        LambdaQueryWrapper<ApCoupon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApCoupon::getType, Const.TYPE_USE).eq(ApCoupon::getDeleteFlag, Const.TYPE_REGISTER);
        List<ApCoupon> list = this.list(queryWrapper);

        list.forEach(coupon -> {
            Long couponId = coupon.getId();
            LambdaQueryWrapper<ApCouponUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ApCouponUser::getCouponId, couponId);
            wrapper.eq(ApCouponUser::getUserId, userId);
            wrapper.eq(ApCouponUser::getDeleteFlag, 1);
            long count = couponUserService.count(wrapper);
            if (count > 0) {
                return;
            }
            Integer limit = coupon.getLimit();
            while (limit > 0) {
                ApCouponUser couponUser = new ApCouponUser();
                couponUser.setCouponId(couponId);
                couponUser.setUserId(userId);
                Integer timeType = coupon.getTimeType();
                if (Objects.equals(Const.TIME_TYPE_TIME, timeType)) {
                    couponUser.setStartTime(coupon.getStartTime());
                    couponUser.setEndTime(coupon.getEndTime());
                } else {
                    couponUser.setStartTime(new Date());
                    couponUser.setEndTime(DateUtil.offsetDay(new Date(), coupon.getDays()).toJdkDate());
                }
                couponUserService.save(couponUser);
                limit--;
            }

        });

    }

    @Override
    public IPage<CouponVo> queryList(Integer couponId, Short status, Integer page, Integer limit, String sort, String order) {
        // 构造分页对象
        Page<CouponVo> pages = new Page(1, 10);
        QueryWrapper<CouponVo> queryWrapper = new QueryWrapper<>();
        if (Objects.nonNull(couponId)) {
            queryWrapper.eq("t.id", couponId);
        }
        if (Objects.nonNull(status)) {
            queryWrapper.eq("u.status", status);
        }
        queryWrapper.orderByDesc("u.create_time");

        IPage<CouponVo> productPage = this.baseMapper.getPageVo(pages, queryWrapper);
        return productPage;
    }
}
