package com.xkcoding.orm.jpa.entity;

import com.xkcoding.orm.jpa.entity.base.AbstractAuditModel;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

/**
 * <p>
 * User entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-07 14:06
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "orm_user")
@ToString(callSuper = true)
public class User extends AbstractAuditModel {
    /**
     * Username
     */
    private String name;

    /**
     * Encrypted password
     */
    private String password;

    /**
     * Salt used for encryption
     */
    private String salt;

    /**
     * Email
     */
    private String email;

    /**
     * Mobile phone number
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Status, -1: Tombstone, 0: Disabled, 1: Enabled
     */
    private Integer status;

    /**
     * Last login time
     */
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    /**
     * Associated department table
     * 1. Relationship maintenance end, responsible for the binding and disarming of many-to-many relationships
     * 2, @JoinTable the name attribute of the annotation specifies the name of the association table, joinColumns specifies the name of the foreign key, and is associated with the relationship maintenance side (User)
     * 3, inverseJoinColumns specifies the name of the foreign key, and the relationship to be associated is maintained (Department)
     * 4, in fact, you can not use the @JoinTable annotation, the default generated associated table name is the main table name + underscore + slave table name,
     * That is, the table name is user_department
     * Foreign key name associated to the main table: primary table name + underscore + primary key column name in the primary table, that is, user_id, specified here using referencedColumnName
     * Foreign key name associated to slave table: The attribute name used in the main table for association + underscore + primary key column name of the slave table, department_id
     * The primary table is the table corresponding to the relationship maintenance side, and the slave table is the table corresponding to the relationship maintenance side
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "orm_user_dept", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "dept_id", referencedColumnName = "id"))
    private Collection<Department> departmentList;

}
