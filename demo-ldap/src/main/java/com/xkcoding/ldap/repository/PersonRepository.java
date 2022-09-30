package com.xkcoding.ldap.repository;

import com.xkcoding.ldap.entity.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.naming.Name;

/**
 * PersonRepository
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-08-26 1:02
 */
@Repository
public interface PersonRepository extends CrudRepository<Person, Name> {

    /**
     * Find based on username
     *
     * @param uid username
     * @return com.xkcoding.ldap.entity.Person
     */
    Person findByUid(String uid);
}
