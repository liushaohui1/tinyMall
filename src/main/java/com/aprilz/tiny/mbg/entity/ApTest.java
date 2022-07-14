package com.aprilz.tiny.mbg.entity;

import com.aprilz.tiny.mbg.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * test
 * </p>
 *
 * @author aprilz
 * @since 2022-07-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ap_test")
@ApiModel(value = "ApTest对象", description = "test")
public class ApTest extends BaseEntity<ApTest> {

    private static final long serialVersionUID = 1L;

    @TableField("create_by")
    private String createBy;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_by")
    private String updateBy;

    @TableField("update_time")
    private Date updateTime;

    @ApiModelProperty("用户名称")
    @TableField("username")
    private String username;


    @Override
    public Serializable pkVal() {
        return null;
    }

}
