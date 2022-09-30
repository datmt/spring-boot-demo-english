package com.xkcoding.sharding.jdbc.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Order form
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-03-26 13:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "t_order")
public class Order {
    /**
     * Primary key
     */
    private Long id;
    /**
     * User ID
     */
    private Long userId;

    /**
     * Order ID
     */
    private Long orderId;
    /**
     * Remarks
     */
    private String remark;
}
