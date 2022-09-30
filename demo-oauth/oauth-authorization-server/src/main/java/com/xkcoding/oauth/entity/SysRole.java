package com.xkcoding.oauth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

/**
 * Here it is completely possible to replace it with only one field
 * But after thinking about it, I will simulate the actual situation to put it
 * Character information.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 12:44
 */
@Data
@Table
@Entity
@EqualsAndHashCode(exclude = {"users"})
@ToString(exclude = "users")
public class SysRole {

    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Role name, according to the spring security specification
     * Requires a start with ROLE_.
     */
    private String name;

    /**
     * Character description.
     */
    private String description;

    /**
     * Current role for all users.
     */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Set<SysUser> users;
}
