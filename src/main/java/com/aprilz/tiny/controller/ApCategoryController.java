package com.aprilz.tiny.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.cache.Cache;
import com.aprilz.tiny.common.consts.CacheConst;
import com.aprilz.tiny.mbg.entity.ApCategory;
import com.aprilz.tiny.service.IApCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 类目表 前端控制器
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@RestController
@RequestMapping("/catalog")
@Validated
public class ApCategoryController {


    @Autowired
    private IApCategoryService categoryService;

    @Autowired
    private Cache cache;

    @GetMapping("/getfirstcategory")
    public Object getFirstCategory() {
        // 所有一级分类目录
        LambdaQueryWrapper<ApCategory> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ApCategory::getLevel, "L1").eq(ApCategory::getDeleteFlag, true).orderByAsc(ApCategory::getSortOrder);
        List<ApCategory> l1CatList = categoryService.list(queryWrapper);
        return CommonResult.success(l1CatList);
    }

    @GetMapping("/getsecondcategory")
    public Object getSecondCategory(@NotNull Integer id) {
        // 所有二级分类目录
        LambdaQueryWrapper<ApCategory> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ApCategory::getDeleteFlag, true).eq(ApCategory::getPid, id)
                .orderByAsc(ApCategory::getSortOrder);
        List<ApCategory> currentSubCategory = categoryService.list(queryWrapper);
        return CommonResult.success(currentSubCategory);
    }

    /**
     * 分类详情
     *
     * @param id 分类类目ID。
     *           如果分类类目ID是空，则选择第一个分类类目。
     *           需要注意，这里分类类目是一级类目
     * @return 分类详情
     */
    @GetMapping("/index")
    public Object index(Integer id) {

        // 所有一级分类目录
        LambdaQueryWrapper<ApCategory> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ApCategory::getLevel, "L1").eq(ApCategory::getDeleteFlag, true).orderByAsc(ApCategory::getSortOrder);
        List<ApCategory> l1CatList = categoryService.list(queryWrapper);

        // 当前一级分类目录
        ApCategory currentCategory = null;
        if (id != null) {
            currentCategory = categoryService.getById(id);
        } else {
            if (l1CatList.size() > 0) {
                currentCategory = l1CatList.get(0);
            }
        }

        // 当前一级分类目录对应的二级分类目录
        List<ApCategory> currentSubCategory = null;
        if (null != currentCategory) {
            queryWrapper.clear();
            queryWrapper.eq(ApCategory::getDeleteFlag, true).eq(ApCategory::getPid, currentCategory.getId())
                    .orderByAsc(ApCategory::getSortOrder);
            currentSubCategory = categoryService.list(queryWrapper);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("categoryList", l1CatList);
        data.put("currentCategory", currentCategory);
        data.put("currentSubCategory", currentSubCategory);
        return CommonResult.success(data);
    }

    /**
     * 所有分类数据
     *
     * @return 所有分类数据
     */
    @GetMapping("/all")
    public CommonResult queryAll() {
        //优先从缓存中读取
        String catalog = cache.getString(CacheConst.CATALOG);
        if (StrUtil.isNotBlank(catalog)) {
            return CommonResult.success(JSONUtil.toBean(catalog, HashMap.class));
        }

        // 所有一级分类目录
        LambdaQueryWrapper<ApCategory> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ApCategory::getLevel, "L1").eq(ApCategory::getDeleteFlag, true).orderByAsc(ApCategory::getSortOrder);
        List<ApCategory> l1CatList = categoryService.list(queryWrapper);

        //所有子分类列表
        Map<Long, List<ApCategory>> allList = new HashMap<>();
        List<ApCategory> sub;
        for (ApCategory category : l1CatList) {
            queryWrapper.clear();
            queryWrapper.eq(ApCategory::getDeleteFlag, true).eq(ApCategory::getPid, category.getId())
                    .orderByAsc(ApCategory::getSortOrder);
            sub = categoryService.list(queryWrapper);
            allList.put(category.getId(), sub);
        }

        // 当前一级分类目录
        ApCategory currentCategory = l1CatList.get(0);

        // 当前一级分类目录对应的二级分类目录
        List<ApCategory> currentSubCategory = null;
        if (null != currentCategory) {
            queryWrapper.clear();
            queryWrapper.eq(ApCategory::getDeleteFlag, true).eq(ApCategory::getPid, currentCategory.getId())
                    .orderByAsc(ApCategory::getSortOrder);
            currentSubCategory = categoryService.list(queryWrapper);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("categoryList", l1CatList);
        data.put("allList", allList);
        data.put("currentCategory", currentCategory);
        data.put("currentSubCategory", currentSubCategory);

        //缓存数据
        cache.put(CacheConst.CATALOG, JSONUtil.toJsonStr(data), 240L);
        return CommonResult.success(data);
    }

    /**
     * 当前分类栏目
     *
     * @param id 分类类目ID
     * @return 当前分类栏目
     */
    @GetMapping("/current")
    public CommonResult current(@NotNull Integer id) {
        // 当前分类
        ApCategory currentCategory = categoryService.getById(id.longValue());
        if (currentCategory == null) {
            return CommonResult.validateFailed();
        }
        LambdaQueryWrapper<ApCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApCategory::getDeleteFlag, true).eq(ApCategory::getPid, currentCategory.getId())
                .orderByAsc(ApCategory::getSortOrder);
        List<ApCategory> currentSubCategory = categoryService.list(queryWrapper);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("currentCategory", currentCategory);
        data.put("currentSubCategory", currentSubCategory);
        return CommonResult.success(data);
    }
}
