package com.aprilz.tiny.mbg.entity;

import com.aprilz.tiny.mbg.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 意见反馈表
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ap_feedback")
@ApiModel(value = "ApFeedback对象", description = "意见反馈表")
public class ApFeedback extends BaseEntity<ApFeedback> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户表的用户ID")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty("用户名称")
    @TableField("username")
    private String username;

    @ApiModelProperty("手机号")
    @TableField("mobile")
    private String mobile;

    @ApiModelProperty("反馈类型")
    @TableField("feed_type")
    private String feedType;

    @ApiModelProperty("反馈内容")
    @TableField("content")
    private String content;

    @ApiModelProperty("状态")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("是否含有图片")
    @TableField("has_picture")
    private Boolean hasPicture;

    @ApiModelProperty("图片地址列表，采用JSON数组格式")
    @TableField("pic_urls")
    private String picUrls;


    @Override
    public Serializable pkVal() {
        return null;
    }

}
