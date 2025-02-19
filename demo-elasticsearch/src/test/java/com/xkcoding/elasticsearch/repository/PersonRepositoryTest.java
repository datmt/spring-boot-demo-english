package com.xkcoding.elasticsearch.repository;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.xkcoding.elasticsearch.SpringBootDemoElasticsearchApplicationTests;
import com.xkcoding.elasticsearch.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;

/**
 * <p>
 * Test the Repository operation ES
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-20 19:03
 */
@Slf4j
public class PersonRepositoryTest extends SpringBootDemoElasticsearchApplicationTests {
    @Autowired
    private PersonRepository repo;

    /**
     * New to testing
     */
    @Test
    public void save() {
        Person person = new Person(1L, "刘备", "蜀国", 18, DateUtil.parse("1990-01-02 03:04:05"), "刘备（161年－223年6月10日），即汉昭烈帝（221年－223年在位），又称先主，字玄德，东汉末年幽州涿郡涿县（今河北省涿州市）人，西汉中山靖王刘胜之后，三国时期蜀汉开国皇帝、政治家。\n刘备少年时拜卢植为师；早年颠沛流离，备尝艰辛，投靠过多个诸侯，曾参与镇压黄巾起义。先后率军救援北海相孔融、徐州牧陶谦等。陶谦病亡后，将徐州让与刘备。赤壁之战时，刘备与孙权联盟击败曹操，趁势夺取荆州。而后进取益州。于章武元年（221年）在成都称帝，国号汉，史称蜀或蜀汉。《三国志》评刘备的机权干略不及曹操，但其弘毅宽厚，知人待士，百折不挠，终成帝业。刘备也称自己做事“每与操反，事乃成尔”。\n章武三年（223年），刘备病逝于白帝城，终年六十三岁，谥号昭烈皇帝，庙号烈祖，葬惠陵。后世有众多文艺作品以其为主角，在成都武侯祠有昭烈庙为纪念。");
        Person save = repo.save(person);
        log.info("【save】= {}", save);
    }

    /**
     * Test batch added
     */
    @Test
    public void saveList() {
        List<Person> personList = Lists.newArrayList();
        personList.add(new Person(2L, "曹操", "魏国", 20, DateUtil.parse("1988-01-02 03:04:05"), "曹操（155年－220年3月15日），字孟德，一名吉利，小字阿瞒，沛国谯县（今安徽亳州）人。东汉末年杰出的政治家、军事家、文学家、书法家，三国中曹魏政权的奠基人。\n曹操曾担任东汉丞相，后加封魏王，奠定了曹魏立国的基础。去世后谥号为武王。其子曹丕称帝后，追尊为武皇帝，庙号太祖。\n东汉末年，天下大乱，曹操以汉天子的名义征讨四方，对内消灭二袁、吕布、刘表、马超、韩遂等割据势力，对外降服南匈奴、乌桓、鲜卑等，统一了中国北方，并实行一系列政策恢复经济生产和社会秩序，扩大屯田、兴修水利、奖励农桑、重视手工业、安置流亡人口、实行“租调制”，从而使中原社会渐趋稳定、经济出现转机。黄河流域在曹操统治下，政治渐见清明，经济逐步恢复，阶级压迫稍有减轻，社会风气有所好转。曹操在汉朝的名义下所采取的一些措施具有积极作用。\n曹操军事上精通兵法，重贤爱才，为此不惜一切代价将看中的潜能分子收于麾下；生活上善诗歌，抒发自己的政治抱负，并反映汉末人民的苦难生活，气魄雄伟，慷慨悲凉；散文亦清峻整洁，开启并繁荣了建安文学，给后人留下了宝贵的精神财富，鲁迅评价其为“改造文章的祖师”。同时曹操也擅长书法，唐朝张怀瓘在《书断》将曹操的章草评为“妙品”。"));
        personList.add(new Person(3L, "孙权", "吴国", 19, DateUtil.parse("1989-01-02 03:04:05"), "孙权（182年－252年5月21日），字仲谋，吴郡富春（今浙江杭州富阳区）人。三国时代孙吴的建立者（229年－252年在位）。\n孙权的父亲孙坚和兄长孙策，在东汉末年群雄割据中打下了江东基业。建安五年（200年），孙策遇刺身亡，孙权继之掌事，成为一方诸侯。建安十三年（208年），与刘备建立孙刘联盟，并于赤壁之战中击败曹操，奠定三国鼎立的基础。建安二十四年（219年），孙权派吕蒙成功袭取刘备的荆州，使领土面积大大增加。\n黄武元年（222年），孙权被魏文帝曹丕册封为吴王，建立吴国。同年，在夷陵之战中大败刘备。黄龙元年（229年），在武昌正式称帝，国号吴，不久后迁都建业。孙权称帝后，设置农官，实行屯田，设置郡县，并继续剿抚山越，促进了江南经济的发展。在此基础上，他又多次派人出海。黄龙二年（230年），孙权派卫温、诸葛直抵达夷州。\n孙权晚年在继承人问题上反复无常，引致群下党争，朝局不稳。太元元年（252年）病逝，享年七十一岁，在位二十四年，谥号大皇帝，庙号太祖，葬于蒋陵。\n孙权亦善书，唐代张怀瓘在《书估》中将其书法列为第三等。"));
        personList.add(new Person(4L, "诸葛亮", "蜀国", 16, DateUtil.parse("1992-01-02 03:04:05"), "诸葛亮（181年-234年10月8日），字孔明，号卧龙，徐州琅琊阳都（今山东临沂市沂南县）人，三国时期蜀国丞相，杰出的政治家、军事家、外交家、文学家、书法家、发明家。\n早年随叔父诸葛玄到荆州，诸葛玄死后，诸葛亮就在襄阳隆中隐居。后刘备三顾茅庐请出诸葛亮，联孙抗曹，于赤壁之战大败曹军。形成三国鼎足之势，又夺占荆州。建安十六年（211年），攻取益州。继又击败曹军，夺得汉中。蜀章武元年（221年），刘备在成都建立蜀汉政权，诸葛亮被任命为丞相，主持朝政。蜀后主刘禅继位，诸葛亮被封为武乡侯，领益州牧。勤勉谨慎，大小政事必亲自处理，赏罚严明；与东吴联盟，改善和西南各族的关系；实行屯田政策，加强战备。前后六次北伐中原，多以粮尽无功。终因积劳成疾，于蜀建兴十二年（234年）病逝于五丈原（今陕西宝鸡岐山境内），享年54岁。刘禅追封其为忠武侯，后世常以武侯尊称诸葛亮。东晋政权因其军事才能特追封他为武兴王。\n诸葛亮散文代表作有《出师表》《诫子书》等。曾发明木牛流马、孔明灯等，并改造连弩，叫做诸葛连弩，可一弩十矢俱发。诸葛亮一生“鞠躬尽瘁、死而后已”，是中国传统文化中忠臣与智者的代表人物。"));
        Iterable<Person> people = repo.saveAll(personList);
        log.info("【people】= {}", people);
    }

    /**
     * Test updates
     */
    @Test
    public void update() {
        repo.findById(1L).ifPresent(person -> {
            person.setRemark(person.getRemark() + "\n更新更新更新更新更新");
            Person save = repo.save(person);
            log.info("【save】= {}", save);
        });
    }

    /**
     * Test removal
     */
    @Test
    public void delete() {
        Primary key deletion
        repo.deleteById(1L);

        The object is deleted
        repo.findById(2L).ifPresent(person -> repo.delete(person));

        Bulk delete
        repo.deleteAll(repo.findAll());
    }

    /**
     * Test normal queries in reverse order of birthdays
     */
    @Test
    public void select() {
        repo.findAll(Sort.by(Sort.Direction.DESC, "birthday")).forEach(person -> log.info("{} 生日: {}", person.getName(), DateUtil.formatDateTime(person.getBirthday())));
    }

    /**
     * Custom queries, based on age range
     */
    @Test
    public void customSelectRangeOfAge() {
        repo.findByAgeBetween(18, 19).forEach(person -> log.info("{} 年龄: {}", person.getName(), person.getAge()));
    }

    /**
     * Advanced queries
     */
    @Test
    public void advanceSelect() {
        QueryBuilders provides a number of static methods to encapsulate most query conditions
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "孙权");
        log.info("【queryBuilder】= {}", queryBuilder.toString());

        repo.search(queryBuilder).forEach(person -> log.info("【person】= {}", person));
    }

    /**
     * Customize advanced queries
     */
    @Test
    public void customAdvanceSelect() {
        Construct query criteria
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        Add basic word breaker conditions
        queryBuilder.withQuery(QueryBuilders.matchQuery("remark", "东汉"));
        Sort criteria
        queryBuilder.withSort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
        Paging conditions
        queryBuilder.withPageable(PageRequest.of(0, 2));
        Page<Person> people = repo.search(queryBuilder.build());
        log.info("【people】总条数 = {}", people.getTotalElements());
        log.info("【people】总页数 = {}", people.getTotalPages());
        people.forEach(person -> log.info("【person】= {}，年龄 = {}", person.getName(), person.getAge()));
    }

    /**
     * Test aggregation, test average age
     */
    @Test
    public void agg() {
        Construct query criteria
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        No results are queried
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));

        Average age
        queryBuilder.addAggregation(AggregationBuilders.avg("avg").field("age"));

        log.info("【queryBuilder】= {}", JSONUtil.toJsonStr(queryBuilder.build()));

        AggregatedPage<Person> people = (AggregatedPage<Person>) repo.search(queryBuilder.build());
        double avgAge = ((InternalAvg) people.getAggregation("avg")).getValue();
        log.info("【avgAge】= {}", avgAge);
    }

    /**
     * Test advanced aggregate queries, there are several people in each country, and what is the average age of each country
     */
    @Test
    public void advanceAgg() {
        Construct query criteria
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        No results are queried
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));

         1. Add a new aggregation with the aggregation type as terms, the aggregation name as country, and the aggregation field as age
        queryBuilder.addAggregation(AggregationBuilders.terms("country").field("country")
             2. Nested aggregations are performed within the national aggregation bucket to find the average age
            .subAggregation(AggregationBuilders.avg("avg").field("age")));

        log.info("【queryBuilder】= {}", JSONUtil.toJsonStr(queryBuilder.build()));

         3. Inquire
        AggregatedPage<Person> people = (AggregatedPage<Person>) repo.search(queryBuilder.build());

         4. parse
         4.1. Take the aggregation named country from the result, because it is a term aggregation using a String type field, so the result is strongly converted to the StringTerm type
        StringTerms country = (StringTerms) people.getAggregation("country");
         4.2. Gets the bucket
        List<StringTerms.Bucket> buckets = country.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
             4.3. Get the key in the bucket, which is the country name 4.4. Gets the number of documents in the bucket
            log.info("{} 总共有 {} 人", bucket.getKeyAsString(), bucket.getDocCount());
             4.5. To get the sub-aggregate result:
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("avg");
            log.info("平均年龄：{}", avg);
        }
    }

}
