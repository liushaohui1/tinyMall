package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.utils.UserUtil;
import com.aprilz.tiny.mbg.entity.ApKeyword;
import com.aprilz.tiny.mbg.entity.ApSearchHistory;
import com.aprilz.tiny.mbg.entity.ApUser;
import com.aprilz.tiny.service.IApKeywordService;
import com.aprilz.tiny.service.IApSearchHistoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.*;

/**
 * @description: 商品搜索服务
 * @author: Aprilz
 * @since: 2022/7/21
 **/
@RestController
@RequestMapping("/search")
@Validated
public class ApSearchController {


    @Autowired
    private IApKeywordService keywordsService;
    @Autowired
    private IApSearchHistoryService searchHistoryService;

    /**
     * 搜索页面信息
     * <p>
     * 如果用户已登录，则给出用户历史搜索记录；
     * 如果没有登录，则给出空历史搜索记录。
     *
     * @return 搜索页面信息
     */
    @GetMapping("/index")
    public CommonResult index() {
        ApUser user = UserUtil.getUser();
        //取出输入框默认的关键词
        LambdaQueryWrapper<ApKeyword> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApKeyword::getIsDefault, true).eq(ApKeyword::getDeleteFlag, true).last("limit 1");
        ApKeyword defaultKeyword = keywordsService.getOne(queryWrapper);
        //取出热门关键词
        queryWrapper.clear();
        queryWrapper.eq(ApKeyword::getIsHot, true).eq(ApKeyword::getDeleteFlag, true).orderByAsc(ApKeyword::getSortOrder);
        List<ApKeyword> hotKeywordList = keywordsService.list(queryWrapper);

        List<ApSearchHistory> historyList;
        if (Objects.nonNull(user) && Objects.nonNull(user.getId())) {
            //取出用户历史关键字
            QueryWrapper<ApSearchHistory> historyQueryWrapper = new QueryWrapper<>();
            historyQueryWrapper.select("distinct keyword").lambda().eq(ApSearchHistory::getUserId, user.getId()).eq(ApSearchHistory::getDeleteFlag, true);
            historyList = searchHistoryService.list(historyQueryWrapper);
        } else {
            historyList = new ArrayList<>(0);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("defaultKeyword", defaultKeyword);
        data.put("historyKeywordList", historyList);
        data.put("hotKeywordList", hotKeywordList);
        return CommonResult.success(data);
    }

    /**
     * 关键字提醒
     * <p>
     * 当用户输入关键字一部分时，可以推荐系统中合适的关键字。
     *
     * @param keyword 关键字
     * @return 合适的关键字
     */
    @GetMapping("/helper")
    public CommonResult helper(@NotEmpty String keyword,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer limit) {
        Page<ApKeyword> pages = new Page(page, limit);
        QueryWrapper<ApKeyword> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct keyword").lambda().like(ApKeyword::getKeyword, keyword).eq(ApKeyword::getDeleteFlag, true);
        Page<ApKeyword> result = keywordsService.page(pages, queryWrapper);
        List<ApKeyword> keywordsList = result.getRecords();
        String[] keys = new String[keywordsList.size()];
        int index = 0;
        for (ApKeyword key : keywordsList) {
            keys[index++] = key.getKeyword();
        }
        return CommonResult.success(keys);
    }

    /**
     * 清除用户搜索历史
     *
     * @return 清理是否成功
     */
    @PostMapping("/clearhistory")
    public CommonResult clearhistory() {
        ApUser user = UserUtil.getUser();
        if (user == null) {
            return CommonResult.unauthorized(null);
        }
        searchHistoryService.deleteByUid(user.getId());
        return CommonResult.success();
    }
}
