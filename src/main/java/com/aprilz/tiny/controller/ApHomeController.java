package com.aprilz.tiny.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.cache.Cache;
import com.aprilz.tiny.common.consts.CacheConst;
import com.aprilz.tiny.common.utils.UserUtil;
import com.aprilz.tiny.config.init.SystemConfig;
import com.aprilz.tiny.mbg.entity.ApAd;
import com.aprilz.tiny.mbg.entity.ApCategory;
import com.aprilz.tiny.mbg.entity.ApCoupon;
import com.aprilz.tiny.mbg.entity.ApUser;
import com.aprilz.tiny.service.*;
import com.aprilz.tiny.vo.GrouponRuleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

/**
 * @description: 首页
 * @author: liushaohui
 * @since: 2022/7/18
 **/
@RestController
@RequestMapping("/home")
public class ApHomeController {

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);
    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();
    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(9, 9, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);
    @Resource
    private IApAdService adService;
    @Resource
    private IApCategoryService categoryService;
    @Resource
    private IApCouponService couponService;
    @Resource
    private IApGoodsService goodsService;
    @Resource
    private IApBrandService brandService;
    @Resource
    private IApTopicService topicService;
    @Resource
    private IApGrouponRulesService grouponRulesService;
    @Autowired
    private Cache cache;


    @GetMapping("/index")
    public CommonResult index() {
        ApUser user = UserUtil.getUser();
        boolean isLogin = Objects.nonNull(user);
        //优先从缓存中读取
        String index;
        if (!isLogin) {
            index = cache.getString(CacheConst.INDEX);
        } else {
            index = cache.getString(CacheConst.INDEX + user.getId());
        }
        if (StrUtil.isNotBlank(index)) {
            return CommonResult.success(JSONUtil.toBean(index, HashMap.class));
        }

        LambdaQueryWrapper<ApAd> banner = new LambdaQueryWrapper();
        banner.eq(ApAd::getPosition, 1).eq(ApAd::getDeleteFlag, true).eq(ApAd::getEnabled, true)
                .le(ApAd::getStartTime, new Date()).ge(ApAd::getEndTime, new Date());
        Callable<List> bannerListCallable = () -> adService.list(banner);

        LambdaQueryWrapper<ApCategory> category = new LambdaQueryWrapper();
        category.select(ApCategory::getId, ApCategory::getName, ApCategory::getIconUrl).eq(ApCategory::getLevel, "L1").eq(ApCategory::getDeleteFlag, true)
                .last("limit 0,8");
        Callable<List> channelListCallable = () -> categoryService.list(category);

        Callable<List> couponListCallable;
        if (!isLogin) {
            LambdaQueryWrapper<ApCoupon> coupon = new LambdaQueryWrapper();
            coupon.ne(ApCoupon::getType, 2).eq(ApCoupon::getDeleteFlag, true).eq(ApCoupon::getStatus, 1).orderByDesc(ApCoupon::getCreateTime).last("limit 3");
            couponListCallable = () -> couponService.list(coupon);
        } else {
            couponListCallable = () -> couponService.queryAvailableList(user.getId());
        }
        //查询新品
        Callable<List> newGoodsListCallable = () -> goodsService.queryByNew(0, Optional.ofNullable(cache.getInteger(SystemConfig.LITEMALL_WX_INDEX_NEW)).orElse(6));

        //查询热门商品
        Callable<List> hotGoodsListCallable = () -> goodsService.queryByHot(0, Optional.ofNullable(cache.getInteger(SystemConfig.LITEMALL_WX_INDEX_HOT)).orElse(6));

        //查询品牌商
        Callable<List> brandListCallable = () -> brandService.query(0, Optional.ofNullable(cache.getInteger(SystemConfig.LITEMALL_WX_INDEX_BRAND)).orElse(4));

        //专题精选栏目
        Callable<List> topicListCallable = () -> topicService.query(0, Optional.ofNullable(cache.getInteger(SystemConfig.LITEMALL_WX_INDEX_BRAND)).orElse(4));

        //团购专区
        Callable<IPage<GrouponRuleVo>> grouponListCallable = () -> grouponRulesService.queryPage(0, 5);

        //查询分类以及旗下的产品
        Callable<List<Map>> floorGoodsListCallable = () -> categoryService.queryIndex();

        FutureTask<List> bannerTask = new FutureTask<>(bannerListCallable);
        FutureTask<List> channelTask = new FutureTask<>(channelListCallable);
        FutureTask<List> couponListTask = new FutureTask<>(couponListCallable);
        FutureTask<List> newGoodsListTask = new FutureTask<>(newGoodsListCallable);
        FutureTask<List> hotGoodsListTask = new FutureTask<>(hotGoodsListCallable);
        FutureTask<List> brandListTask = new FutureTask<>(brandListCallable);
        FutureTask<List> topicListTask = new FutureTask<>(topicListCallable);
        FutureTask<IPage<GrouponRuleVo>> grouponListTask = new FutureTask<>(grouponListCallable);
        FutureTask<List> floorGoodsListTask = new FutureTask(floorGoodsListCallable);

        executorService.submit(bannerTask);
        executorService.submit(channelTask);
        executorService.submit(couponListTask);
        executorService.submit(newGoodsListTask);
        executorService.submit(hotGoodsListTask);
        executorService.submit(brandListTask);
        executorService.submit(topicListTask);
        executorService.submit(grouponListTask);
        executorService.submit(floorGoodsListTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("banner", bannerTask.get());
            entity.put("channel", channelTask.get());
            entity.put("couponList", couponListTask.get());
            entity.put("newGoodsList", newGoodsListTask.get());
            entity.put("hotGoodsList", hotGoodsListTask.get());
            entity.put("brandList", brandListTask.get());
            entity.put("topicList", topicListTask.get());
            entity.put("grouponList", grouponListTask.get().getRecords());
            entity.put("floorGoodsList", floorGoodsListTask.get());
            //缓存数据
            if (!isLogin) {
                cache.put(CacheConst.INDEX, JSONUtil.toJsonStr(entity), 240L);
            } else {
                cache.put(CacheConst.INDEX + user.getId(), JSONUtil.toJsonStr(entity), 240L);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            executorService.shutdown();
//        }
        return CommonResult.success(entity);

    }


}
