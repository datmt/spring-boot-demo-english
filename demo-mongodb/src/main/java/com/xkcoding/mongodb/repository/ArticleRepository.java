package com.xkcoding.mongodb.repository;

import com.xkcoding.mongodb.model.Article;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * <p>
 * Article Dao
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-28 16:30
 */
public interface ArticleRepository extends MongoRepository<Article, Long> {
    /**
     * Fuzzy query based on title
     *
     * @param title title
     * @return list of articles that meet the criteria
     */
    List<Article> findByTitleLike(String title);
}
