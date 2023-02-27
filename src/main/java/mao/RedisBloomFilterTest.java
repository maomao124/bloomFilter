package mao;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * Project name(项目名称)：布隆过滤器
 * Package(包名): mao
 * Class(类名): RedisBloomFilterTest
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/2/27
 * Time(创建时间)： 23:13
 * Version(版本): 1.0
 * Description(描述)： 测试redis的布隆过滤器，因为有网络io，所以很慢
 */

public class RedisBloomFilterTest
{
    public static void main(String[] args)
    {
        //redis的布隆过滤器
        RedisBloomFilter redisBloomFilter = new RedisBloomFilterImpl("127.0.0.1", 16379);

        //1500次循环
        for (int i = 0; i < 1500; i++)
        {
            if (i % 3 == 0)
            {
                continue;
            }
            //将i的值% 3 不等于 0 的值放进去
            redisBloomFilter.add("filter11", String.valueOf(i));
        }

        //存在的计数
        int count = 0;
        //统计
        for (int i = 0; i < 1500; i++)
        {
            //判断是否存在
            boolean b = redisBloomFilter.exists("filter11", String.valueOf(i));
            //System.out.println(i + " --> " + b);
            //可能存在
            if (b)
            {
                count++;
            }
        }

        System.out.println("预期结果：1000，最终结果：" + count);
        System.out.println();

        redisBloomFilter.close();
    }
}
