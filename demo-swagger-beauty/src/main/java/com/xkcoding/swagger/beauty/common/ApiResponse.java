package com.xkcoding.swagger.beauty.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * Generic API interface returns
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-28 14:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "通用PI接口返回", description = "Common Api Response")
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = -8987146499044811408L;
    /**
     * Universal return status
     */
    @ApiModelProperty(value = "通用返回状态", required = true)
    private Integer code;
    /**
     * Generic return information
     */
    @ApiModelProperty(value = "通用返回信息", required = true)
    private String message;
    /**
     * Generic return data
     */
    @ApiModelProperty(value = "通用返回数据", required = true)
    private T data;
}
