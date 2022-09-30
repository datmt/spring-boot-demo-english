package com.xkcoding.mongodb.repository;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.xkcoding.mongodb.SpringBootDemoMongodbApplicationTests;
import com.xkcoding.mongodb.model.Article;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Test operation MongoDb
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-28 16:35
 */
@Slf4j
public class ArticleRepositoryTest extends SpringBootDemoMongodbApplicationTests {
    @Autowired
    private ArticleRepository articleRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Snowflake snowflake;

    /**
     * New to testing
     */
    @Test
    public void testSave() {
        Article article = new Article(1L, RandomUtil.randomString(20), RandomUtil.randomString(150), DateUtil.date(), DateUtil.date(), 0L, 0L);
        articleRepo.save(article);
        log.info("【article】= {}", JSONUtil.toJsonStr(article));
    }

    /**
     * Test new list
     */
    @Test
    public void testSaveList() {
        List<Article> articles = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            articles.add(new Article(snowflake.nextId(), RandomUtil.randomString(20), RandomUtil.randomString(150), DateUtil.date(), DateUtil.date(), 0L, 0L));
        }
        articleRepo.saveAll(articles);

        log.info("【articles】= {}", JSONUtil.toJsonStr(articles.stream().map(Article::getId).collect(Collectors.toList())));
    }

    /**
     * Test updates
     */
    @Test
    public void testUpdate() {
        articleRepo.findById(1L).ifPresent(article -> {
            article.setTitle(article.getTitle() + "更新之后的标题");
            article.setUpdateTime(DateUtil.date());
            articleRepo.save(article);
            log.info("【article】= {}", JSONUtil.toJsonStr(article));
        });
    }

    /**
     * Test removal
     */
    @Test
    public void testDelete() {
        Delete based on the primary key
        articleRepo.deleteById(1L);

        Delete All
        articleRepo.deleteAll();
    }

    /**
     * Test the number of likes and visitors, and use save to update likes and visitors
     */
    @Test
    public void testThumbUp() {
        articleRepo.findById(1L).ifPresent(article -> {
            article.setThumbUp(article.getThumbUp() + 1);
            article.setVisits(article.getVisits() + 1);
            articleRepo.save(article);
            log.info("【标题】= {}【点赞数】= {}【访客数】= {}", article.getTitle(), article.getThumbUp(), article.getVisits());
        });
    }

    /**
     * Test likes, visitors, and update likes and visitors in a more elegant/efficient way
     */
    @Test
    public void testThumbUp2() {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(1L));
        Update update = new Update();
        update.inc("thumbUp", 1L);
        update.inc("visits", 1L);
        mongoTemplate.updateFirst(query, update, "article");

        articleRepo.findById(1L).ifPresent(article -> log.info("【标题】= {}【点赞数】= {}【访客数】= {}", article.getTitle(), article.getThumbUp(), article.getVisits()));
    }

    /**
     * Test paginated sort query
     */
    @Test
    public void testQuery() {
        Sort sort = Sort.by("thumbUp", "updateTime").descending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        Page<Article> all = articleRepo.findAll(pageRequest);
        log.info("【总页数】= {}", all.getTotalPages());
        log.info("【总条数】= {}", all.getTotalElements());
        log.info("【当前页数据】= {}", JSONUtil.toJsonStr(all.getContent().stream().map(article -> "文章标题：" + article.getTitle() + "点赞数：" + article.getThumbUp() + "更新时间：" + article.getUpdateTime()).collect(Collectors.toList())));
    }

    /**
     * Test fuzzy query based on title
     */
    @Test
    public void testFindByTitleLike() {
        List<Article> articles = articleRepo.findByTitleLike("更新");
        log.info("【articles】= {}", JSONUtil.toJsonStr(articles));
    }

}
