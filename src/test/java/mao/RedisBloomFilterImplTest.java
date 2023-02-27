package mao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Project name(项目名称)：布隆过滤器
 * Package(包名): mao
 * Class(测试类名): RedisBloomFilterImplTest
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/2/27
 * Time(创建时间)： 22:36
 * Version(版本): 1.0
 * Description(描述)： 测试类
 */

class RedisBloomFilterImplTest
{

    private static RedisBloomFilterImpl redisBloomFilter;

    @BeforeAll
    static void beforeAll()
    {
        redisBloomFilter = new RedisBloomFilterImpl("127.0.0.1", 16379);
    }

    @AfterAll
    static void afterAll()
    {
        redisBloomFilter.close();
    }

    @Test
    void add()
    {
        System.out.println(redisBloomFilter.add("filter3", "1"));
        System.out.println(redisBloomFilter.add("filter3", "2"));
        System.out.println(redisBloomFilter.add("filter3", "3"));
        System.out.println(redisBloomFilter.add("filter3", "4"));
        System.out.println(redisBloomFilter.add("filter3", "5"));

    }

    @Test
    void mAdd()
    {
        List<Boolean> filter4 = redisBloomFilter.mAdd("filter4", "1", "3", "4", "6");
        System.out.println(filter4);
        List<Boolean> filter44 = redisBloomFilter.mAdd("filter4", "1", "3", "4", "7");
        System.out.println(filter44);
    }

    @Test
    void exists()
    {
        System.out.println(redisBloomFilter.exists("filter3", "1"));
        System.out.println(redisBloomFilter.exists("filter3", "2"));
        System.out.println(redisBloomFilter.exists("filter3", "4"));
        System.out.println(redisBloomFilter.exists("filter3", "5"));
        System.out.println(redisBloomFilter.exists("filter3", "6"));
        System.out.println(redisBloomFilter.exists("filter3", "9"));

        System.out.println(redisBloomFilter.exists("filter4", "6"));
        System.out.println(redisBloomFilter.exists("filter4", "7"));
        System.out.println(redisBloomFilter.exists("filter4", "9"));
    }

    @Test
    void mExists()
    {
        List<Boolean> filter3 = redisBloomFilter.mExists
                ("filter3", "1", "2", "3", "4", "5", "6", "7", "8");
        System.out.println(filter3);

        List<Boolean> filter4 = redisBloomFilter.mExists(
                "filter4", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
        );
        System.out.println(filter4);
    }


}
