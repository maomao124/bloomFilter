package mao;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * Project name(项目名称)：布隆过滤器
 * Package(包名): mao
 * Class(类名): GuavaBloomFilter2
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/2/27
 * Time(创建时间)： 20:12
 * Version(版本): 1.0
 * Description(描述)： 测试布隆过滤器误差
 */

public class GuavaBloomFilter2
{
    public static void main(String[] args)
    {
        testGuavaBloomFilter(10000, 0.01);
        testGuavaBloomFilter(10000, 0.02);
        testGuavaBloomFilter(10000, 0.05);
        testGuavaBloomFilter(10000, 0.1);
        testGuavaBloomFilter(10000, 0.5);

        System.out.println("------");
        testGuavaBloomFilter(3000, 0.01);
        testGuavaBloomFilter(3000, 0.02);
        testGuavaBloomFilter(3000, 0.05);
        testGuavaBloomFilter(3000, 0.1);
        testGuavaBloomFilter(3000, 0.5);

        System.out.println("------");
        testGuavaBloomFilter(2000, 0.01);
        testGuavaBloomFilter(2000, 0.02);
        testGuavaBloomFilter(2000, 0.05);
        testGuavaBloomFilter(2000, 0.1);
        testGuavaBloomFilter(2000, 0.5);

        System.out.println("------");
        testGuavaBloomFilter(1000, 0.01);
        testGuavaBloomFilter(1000, 0.02);
        testGuavaBloomFilter(1000, 0.05);
        testGuavaBloomFilter(1000, 0.1);
        testGuavaBloomFilter(1000, 0.5);
        testGuavaBloomFilter(1000, 0.005);
        testGuavaBloomFilter(1000, 0.001);
        testGuavaBloomFilter(1000, 0.0001);

        System.out.println("------");
        testGuavaBloomFilter(500, 0.01);
        testGuavaBloomFilter(500, 0.02);
        testGuavaBloomFilter(500, 0.05);
        testGuavaBloomFilter(500, 0.1);
        testGuavaBloomFilter(500, 0.5);
        testGuavaBloomFilter(500, 0.005);
        testGuavaBloomFilter(500, 0.001);
        testGuavaBloomFilter(500, 0.0001);

        System.out.println("------");
        testGuavaBloomFilter(200, 0.01);
        testGuavaBloomFilter(200, 0.02);
        testGuavaBloomFilter(200, 0.05);
        testGuavaBloomFilter(200, 0.1);
        testGuavaBloomFilter(200, 0.5);
        testGuavaBloomFilter(200, 0.005);
        testGuavaBloomFilter(200, 0.001);
        testGuavaBloomFilter(200, 0.0001);

        System.out.println("------");
        testGuavaBloomFilter(100, 0.01);
        testGuavaBloomFilter(100, 0.02);
        testGuavaBloomFilter(100, 0.05);
        testGuavaBloomFilter(100, 0.1);
        testGuavaBloomFilter(100, 0.5);
        testGuavaBloomFilter(100, 0.005);
        testGuavaBloomFilter(100, 0.001);
        testGuavaBloomFilter(100, 0.0001);
    }


    /**
     * 测试Guava的布隆过滤器
     *
     * @param expectedInsertions 布隆过滤器最多存放的数量
     * @param fpp                误差
     */
    public static void testGuavaBloomFilter(int expectedInsertions, double fpp)
    {
        //布隆过滤器对象，创建最多存放最多2000个整数的布隆过滤器，误判率为0.01
        BloomFilter<Integer> bloomFilter = BloomFilter.create(
                Funnels.integerFunnel(), expectedInsertions, fpp);

        //1500次循环
        for (int i = 0; i < 1500; i++)
        {
            if (i % 3 == 0)
            {
                continue;
            }
            //将i的值% 3 不等于 0 的值放进去
            bloomFilter.put(i);
        }

        //存在的计数
        int count = 0;
        //统计
        for (int i = 0; i < 1500; i++)
        {
            //判断是否存在
            boolean b = bloomFilter.mightContain(i);
            //System.out.println(i + " --> " + b);
            //可能存在
            if (b)
            {
                count++;
            }
        }
        System.out.println("最大数量：" + expectedInsertions + "  误差：" + fpp);
        System.out.println("预期结果：1000，最终结果：" + count);
        System.out.println();
    }
}
