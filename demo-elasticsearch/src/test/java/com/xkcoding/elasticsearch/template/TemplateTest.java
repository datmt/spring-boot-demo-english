package com.xkcoding.elasticsearch.template;

import com.xkcoding.elasticsearch.SpringBootDemoElasticsearchApplicationTests;
import com.xkcoding.elasticsearch.model.Person;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

/**
 * <p>
 * Test the creation/deletion of ElasticTemplate
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-20 17:46
 */
public class TemplateTest extends SpringBootDemoElasticsearchApplicationTests {
    @Autowired
    private ElasticsearchTemplate esTemplate;

    /**
     * Test ElasticTemplate to create an index
     */
    @Test
    public void testCreateIndex() {
        An index is created based on the @Document annotation information of the Item class
        esTemplate.createIndex(Person.class);

        When you configure the mapping, the mapping is automatically completed according to the id, Field, and other fields in the Item class
        esTemplate.putMapping(Person.class);
    }

    /**
     * Test ElasticTemplate to remove index
     */
    @Test
    public void testDeleteIndex() {
        esTemplate.deleteIndex(Person.class);
    }
}
