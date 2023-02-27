package mao;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * Project name(项目名称)：布隆过滤器
 * Package(包名): mao
 * Class(类名): GuavaBloomFilter
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/2/27
 * Time(创建时间)： 20:01
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class GuavaBloomFilter
{
    public static void main(String[] args)
    {
        //布隆过滤器对象，创建最多存放最多2000个整数的布隆过滤器，误判率为0.01
        BloomFilter<Integer> bloomFilter = BloomFilter.create(
                Funnels.integerFunnel(), 2000, 0.01);

        //判断是否存在
        System.out.println(bloomFilter.mightContain(100));
        System.out.println(bloomFilter.mightContain(101));
        System.out.println(bloomFilter.mightContain(102));

        //设置值
        bloomFilter.put(100);
        bloomFilter.put(101);
        bloomFilter.put(102);

        //判断是否存在
        System.out.println(bloomFilter.mightContain(100));
        System.out.println(bloomFilter.mightContain(101));
        System.out.println(bloomFilter.mightContain(102));
        System.out.println(bloomFilter.mightContain(103));
    }
}
