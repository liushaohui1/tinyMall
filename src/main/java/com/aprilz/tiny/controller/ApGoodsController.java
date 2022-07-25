package com.aprilz.tiny.controller;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.consts.Const;
import com.aprilz.tiny.common.utils.CacheDbUtil;
import com.aprilz.tiny.common.utils.UserUtil;
import com.aprilz.tiny.config.init.SystemConfig;
import com.aprilz.tiny.mbg.entity.*;
import com.aprilz.tiny.service.*;
import com.aprilz.tiny.vo.GoodsSpecificationVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.*;

/**
 * <p>
 * 商品基本信息表 前端控制器
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@RestController
@RequestMapping("/goods")
@Validated
public class ApGoodsController {

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    @Resource
    private IApGoodsService goodsService;

    @Resource
    private IApGoodsAttributeService goodsAttributeService;

    @Resource
    private IApGoodsSpecificationService goodsSpecificationService;

    @Resource
    private IApGoodsProductService goodsProductService;

    @Resource
    private IApIssueService issueService;

    @Resource
    private IApBrandService brandService;

    @Resource
    private IApCommentService commentService;

    @Resource
    private IApUserService userService;

    @Resource
    private IApGrouponRulesService grouponRulesService;

    @Autowired
    private IApCollectService collectService;

    @Autowired
    private IApFootprintService footprintService;

    @Autowired
    private IApCategoryService categoryService;

    @Autowired
    private IApSearchHistoryService searchHistoryService;

    /**
     * 商品详情
     * <p>
     * 用户可以不登录。
     * 如果用户登录，则记录用户足迹以及返回用户收藏信息。
     *
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/detail")
    public CommonResult detail(@NotNull @RequestParam("id") Long id) {

        // 商品信息
        LambdaQueryWrapper<ApGoods> goodsQuery = new LambdaQueryWrapper<>();
        goodsQuery.eq(ApGoods::getId, id).eq(ApGoods::getDeleteFlag, true).last("limit 1");
        ApGoods info = goodsService.getOne(goodsQuery);

        // 商品属性
        LambdaQueryWrapper<ApGoodsAttribute> attrQuery = new LambdaQueryWrapper<>();
        attrQuery.eq(ApGoodsAttribute::getGoodsId, id).eq(ApGoodsAttribute::getDeleteFlag, true);
        Callable<List> goodsAttributeListCallable = () -> goodsAttributeService.list(attrQuery);

        // 商品规格 返回的是定制的GoodsSpecificationVo
        Callable<List<GoodsSpecificationVo>> objectCallable = () -> goodsSpecificationService.getSpecificationVoList(id);

        // 商品规格对应的数量和价格
        LambdaQueryWrapper<ApGoodsProduct> productQuery = new LambdaQueryWrapper<>();
        productQuery.eq(ApGoodsProduct::getGoodsId, id).eq(ApGoodsProduct::getDeleteFlag, true);
        Callable<List> productListCallable = () -> goodsProductService.list(productQuery);

        // 商品问题，这里是一些通用问题
        LambdaQueryWrapper<ApIssue> issQuery = new LambdaQueryWrapper<>();
        issQuery.eq(ApIssue::getDeleteFlag, true).orderByDesc(ApIssue::getCreateTime).last("limit 4");
        Callable<List> issueCallable = () -> issueService.list(issQuery);

        // 商品品牌商
        Callable<ApBrand> brandCallable = () -> {
            Integer brandId = info.getBrandId();
            ApBrand brand;
            if (brandId == 0) {
                brand = new ApBrand();
            } else {
                brand = brandService.getById(info.getBrandId());
            }
            return brand;
        };

        // 评论
        Callable<Map> commentsCallable = () -> {
            LambdaQueryWrapper<ApComment> commentQuery = new LambdaQueryWrapper<>();
            commentQuery.eq(ApComment::getValueId, id).eq(ApComment::getType, 0).eq(ApComment::getDeleteFlag, true).orderByDesc(ApComment::getCreateTime);
            long count = commentService.count(commentQuery);
            commentQuery.last("limit 2");
            List<ApComment> comments = commentService.list(commentQuery);
            List<Map<String, Object>> commentsVo = new ArrayList<>(comments.size());
            for (ApComment comment : comments) {
                Map<String, Object> c = new HashMap<>();
                c.put("id", comment.getId());
                c.put("addTime", comment.getCreateTime());
                c.put("content", comment.getContent());
                c.put("adminContent", comment.getAdminContent());
                ApUser apUser = userService.getById(comment.getUserId());
                c.put("nickname", apUser == null ? "" : apUser.getNickname());
                c.put("avatar", apUser == null ? "" : apUser.getAvatar());
                c.put("picList", comment.getPicUrls());
                commentsVo.add(c);
            }
            Map<String, Object> commentList = new HashMap<>();
            commentList.put("count", count);
            commentList.put("data", commentsVo);
            return commentList;
        };

        //团购信息
        LambdaQueryWrapper<ApGrouponRules> grouponRulesQuery = new LambdaQueryWrapper<>();
        grouponRulesQuery.clear();
        grouponRulesQuery.eq(ApGrouponRules::getGoodsId, id).eq(ApGrouponRules::getStatus, Const.RULE_STATUS_ON)
                .eq(ApGrouponRules::getDeleteFlag, true).orderByDesc(ApGrouponRules::getCreateTime);
        Callable<List> grouponRulesCallable = () -> grouponRulesService.list(grouponRulesQuery);

        // 用户收藏
        long userHasCollect = 0L;
        ApUser user = UserUtil.getUser();
        if (Objects.nonNull(user) && user.getId() != null) {
            LambdaQueryWrapper<ApCollect> collectQuery = new LambdaQueryWrapper<>();
            collectQuery.eq(ApCollect::getUserId, user.getId()).eq(ApCollect::getType, 0).eq(ApCollect::getValueId, id)
                    .eq(ApCollect::getDeleteFlag, true);
            userHasCollect = collectService.count(collectQuery);
        }

        // 记录用户的足迹 异步处理
        if (Objects.nonNull(user) && user.getId() != null) {
            executorService.execute(() -> {
                ApFootprint footprint = new ApFootprint();
                footprint.setUserId(Math.toIntExact(user.getId()));
                footprint.setGoodsId(Math.toIntExact(id));
                footprintService.save(footprint);
            });
        }
        FutureTask<List> goodsAttributeListTask = new FutureTask<>(goodsAttributeListCallable);
        FutureTask<List<GoodsSpecificationVo>> objectCallableTask = new FutureTask<>(objectCallable);
        FutureTask<List> productListCallableTask = new FutureTask<>(productListCallable);
        FutureTask<List> issueCallableTask = new FutureTask<>(issueCallable);
        FutureTask<Map> commentsCallableTsk = new FutureTask<>(commentsCallable);
        FutureTask<ApBrand> brandCallableTask = new FutureTask<>(brandCallable);
        FutureTask<List> grouponRulesCallableTask = new FutureTask<>(grouponRulesCallable);

        executorService.submit(goodsAttributeListTask);
        executorService.submit(objectCallableTask);
        executorService.submit(productListCallableTask);
        executorService.submit(issueCallableTask);
        executorService.submit(commentsCallableTsk);
        executorService.submit(brandCallableTask);
        executorService.submit(grouponRulesCallableTask);

        Map<String, Object> data = new HashMap<>();

        try {
            data.put("info", info);
            data.put("userHasCollect", userHasCollect);
            data.put("issue", issueCallableTask.get());
            data.put("comment", commentsCallableTsk.get());
            data.put("specificationList", objectCallableTask.get());
            data.put("productList", productListCallableTask.get());
            data.put("attribute", goodsAttributeListTask.get());
            data.put("brand", brandCallableTask.get());
            data.put("groupon", grouponRulesCallableTask.get());
            data.put("share", BooleanUtil.toBoolean(CacheDbUtil.get(SystemConfig.LITEMALL_WX_SHARE)));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //商品分享图片地址
        data.put("shareImage", info.getShareUrl());
        return CommonResult.success(data);
    }


    /**
     * 商品详情页面“大家都在看”推荐商品
     *
     * @param id, 商品ID
     * @return 商品详情页面推荐商品
     */
    @GetMapping("/related")
    public Object related(@NotNull @RequestParam("id") Long id) {
        LambdaQueryWrapper<ApGoods> goodsQuery = new LambdaQueryWrapper<>();
        goodsQuery.eq(ApGoods::getId, id).eq(ApGoods::getDeleteFlag, true).last("limit 1");
        ApGoods goods = goodsService.getOne(goodsQuery);

        if (goods == null) {
            return CommonResult.success(null);
        }

        // 目前的商品推荐算法仅仅是推荐同类目的其他商品
        int cid = goods.getCategoryId();

        // 查找六个相关商品
        goodsQuery.clear();
        goodsQuery.eq(ApGoods::getCategoryId, cid).eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, true)
                .orderByDesc(ApGoods::getCreateTime).last("limit 6");
        List<ApGoods> goodsList = goodsService.list(goodsQuery);
        return CommonResult.success(goodsList);
    }


    /**
     * 在售的商品总数
     *
     * @return 在售的商品总数
     */
    @GetMapping("/count")
    public CommonResult count() {
        LambdaQueryWrapper<ApGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, true);
        Long goodsCount = goodsService.count(queryWrapper);
        return CommonResult.success(goodsCount);
    }


    /**
     * 商品分类类目
     *
     * @param id 分类类目ID
     * @return 商品分类类目
     */
    @GetMapping("/category")
    public CommonResult category(@NotNull @RequestParam("id") Integer id) {
        LambdaQueryWrapper<ApCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApCategory::getId, id).eq(ApCategory::getDeleteFlag, true);
        ApCategory cur = categoryService.getOne(queryWrapper);
        ApCategory parent;
        List<ApCategory> children;

        if (cur.getPid() == 0) {
            parent = cur;
            queryWrapper.clear();
            queryWrapper.eq(ApCategory::getDeleteFlag, true).eq(ApCategory::getPid, cur.getId())
                    .orderByAsc(ApCategory::getSortOrder);
            children = categoryService.list(queryWrapper);
            cur = children.size() > 0 ? children.get(0) : cur;
        } else {
            queryWrapper.clear();
            queryWrapper.eq(ApCategory::getDeleteFlag, true).eq(ApCategory::getId, cur.getPid())
                    .orderByAsc(ApCategory::getSortOrder);
            parent = categoryService.getOne(queryWrapper);
            queryWrapper.clear();
            queryWrapper.eq(ApCategory::getDeleteFlag, true).eq(ApCategory::getPid, cur.getPid())
                    .orderByAsc(ApCategory::getSortOrder);
            children = categoryService.list(queryWrapper);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("currentCategory", cur);
        data.put("parentCategory", parent);
        data.put("brotherCategory", children);
        return CommonResult.success(data);
    }


    /**
     * 根据条件搜素商品
     * <p>
     * 1. 这里的前五个参数都是可选的，甚至都是空
     * 2. 用户是可选登录，如果登录，则记录用户的搜索关键字
     *
     * @param categoryId 分类类目ID，可选
     * @param brandId    品牌商ID，可选
     * @param keyword    关键字，可选
     * @param isNew      是否新品，可选
     * @param isHot      是否热买，可选
     * @param page       分页页数
     * @param limit      分页大小
     * @param sort       排序方式，支持"add_time", "retail_price"或"name"
     * @param order      排序类型，顺序或者降序
     * @return 根据条件搜素的商品详情
     */
    @GetMapping("/list")
    public CommonResult list(
            Integer categoryId,
            Integer brandId,
            String keyword,
            Boolean isNew,
            Boolean isHot,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "create_time") String sort,
            @RequestParam(defaultValue = "desc") String order) {
        ApUser user = UserUtil.getUser();
        //添加到搜索历史
        if (Objects.nonNull(user) && StrUtil.isNotBlank(keyword)) {
            ApSearchHistory searchHistoryVo = new ApSearchHistory();
            searchHistoryVo.setKeyword(keyword);
            searchHistoryVo.setUserId(Math.toIntExact(user.getId()));
            searchHistoryVo.setFrom("wx");
            searchHistoryService.save(searchHistoryVo);
        }

        //查询列表数据
        Page<ApGoods> goodsPage = goodsService.querySelective(categoryId, brandId, keyword, isHot, isNew, page, limit, sort, order);

        // 查询商品所属类目列表。
        List<Integer> goodsCatIds = goodsService.getCategoryIds(categoryId, brandId, keyword, isHot, isNew);
        List<ApCategory> categoryList;
        if (goodsCatIds.size() != 0) {
            LambdaQueryWrapper<ApCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ApCategory::getId, goodsCatIds).eq(ApCategory::getLevel, "L2").eq(ApCategory::getDeleteFlag, true);
            categoryList = categoryService.list(queryWrapper);
        } else {
            categoryList = new ArrayList<>(0);
        }

        Map<String, Object> entity = new HashMap<>();
        entity.put("list", goodsPage.getRecords());
        entity.put("total", goodsPage.getTotal());
        entity.put("page", goodsPage.getCurrent());
        entity.put("limit", goodsPage.getSize());
        entity.put("pages", goodsPage.getPages());
        entity.put("filterCategoryList", categoryList);

        // 因为这里需要返回额外的filterCategoryList参数，因此不能方便使用ResponseUtil.okList
        return CommonResult.success(entity);
    }

}
