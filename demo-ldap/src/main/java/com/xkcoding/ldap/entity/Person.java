package com.xkcoding.ldap.entity;

import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.io.Serializable;

/**
 * People
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-08-26 0:51
 */
@Data
@Entry(base = "ou=people", objectClasses = {"posixAccount", "inetOrgPerson", "top"})
public class Person implements Serializable {

    private static final long serialVersionUID = -7946768337975852352L;

    @Id
    private Name id;

    /**
     * User ID
     */
    private String uidNumber;

    /**
     * Username
     */
    @DnAttribute(value = "uid", index = 1)
    private String uid;

    /**
     * Name
     */
    @Attribute(name = "cn")
    private String personName;

    /**
     * Password
     */
    private String userPassword;

    /**
     * First name
     */
    private String givenName;

    /**
     * Last name
     */
    @Attribute(name = "sn")
    private String surname;

    /**
     * Email
     */
    private String mail;

    /**
     * Position
     */
    private String title;

    /**
     * Department
     */
    private String departmentNumber;

    /**
     * Department ID
     */
    private String gidNumber;

    /**
     * Root directory
     */
    private String homeDirectory;

    /**
     * loginShell
     */
    private String loginShell;


}
