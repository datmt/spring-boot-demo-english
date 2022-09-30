package com.xkcoding.websocket.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Key-value matching
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-14 17:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KV {
    /**
     * Key
     */
    private String key;

    /**
     5 yuan
     */
    private Object value;
}
