package com.xkcoding.neo4j.repository;

import com.xkcoding.neo4j.model.Class;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

/**
 * <p>
 * Class node Repository
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 15:05
 */
public interface ClassRepository extends Neo4jRepository<Class, String> {
    /**
     * Check class information based on class name
     *
     * @param name class name
     * @return Class information
     */
    Optional<Class> findByName(String name);
}
