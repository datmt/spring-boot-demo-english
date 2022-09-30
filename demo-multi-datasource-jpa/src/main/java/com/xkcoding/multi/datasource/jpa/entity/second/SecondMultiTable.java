package com.xkcoding.multi.datasource.jpa.entity.second;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * Multi-data source test table
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-18 10:06
 */
@Data
@Entity
@Table(name = "multi_table")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecondMultiTable {
    /**
     * Primary key
     */
    @Id
    private Long id;

    /**
     * Name
     */
    private String name;
}
