package com.xkcoding.elasticsearch.repository;

import com.xkcoding.elasticsearch.model.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * <p>
 * User persistence layer
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-20 19:00
 */
public interface PersonRepository extends ElasticsearchRepository<Person, Long> {

    /**
     * Query based on age range
     *
     * @param min minimum
     * @param max value
     * @return list of users who meet the criteria
     */
    List<Person> findByAgeBetween(Integer min, Integer max);
}
