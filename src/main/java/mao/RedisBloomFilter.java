package mao;

import java.util.List;

/**
 * Project name(项目名称)：布隆过滤器
 * Package(包名): mao
 * Interface(接口名): RedisBloomFilter
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/2/27
 * Time(创建时间)： 22:05
 * Version(版本): 1.0
 * Description(描述)： redis布隆过滤器客户端
 */

public interface RedisBloomFilter
{
    /**
     * 关闭链接
     */
    void close();

    /**
     * 将元素添加到布隆过滤器中，如果该过滤器尚不存在，则创建该过滤器。
     *
     * @param filterKey 布隆过滤器的名称
     * @param item      添加的元素
     * @return boolean
     */
    boolean add(String filterKey, String item);

    /**
     * 将一个或多个元素添加到“布隆过滤器”中，并创建一个尚不存在的过滤器。
     * 该命令的操作方式`BF.ADD`与之相同，只不过它允许多个输入并返回多个值。
     *
     * @param filterKey 布隆过滤器的名称
     * @param items     添加的元素
     * @return {@link List}<{@link Boolean}>
     */
    List<Boolean> mAdd(String filterKey, String... items);


    /**
     * 确定元素是否在布隆过滤器中存在
     *
     * @param filterKey 布隆过滤器的名称
     * @param item      添加的元素
     * @return boolean 如果存在，则为true，反之为false
     */
    boolean exists(String filterKey, String item);


    /**
     * 确定一个或者多个元素是否在布隆过滤器中存在
     * 该命令的操作方式`BF.EXISTS`与之相同，只不过它允许多个输入并返回多个值。
     *
     * @param filterKey 布隆过滤器的名称
     * @param items     添加的元素
     * @return {@link List}<{@link Boolean}>
     */
    List<Boolean> mExists(String filterKey, String... items);


    /**
     * 储备
     *
     * @param filterKey  布隆过滤器的名称
     * @param error_rate 期望的误报率。该值必须介于 0 到 1 之间。例如，对于期望的误报率 0.1％（1000 中为 1），
     *                   error_rate 应该设置为 0.001。
     *                   该数字越接近零，则每个项目的内存消耗越大，并且每个操作的 CPU 使用率越高。
     * @param capacity   过滤器的容量。当实际存储的元素个数超过这个值之后，性能将开始下降。
     *                   实际的降级将取决于超出限制的程度。随着过滤器元素数量呈指数增长，性能将线性下降。
     * @return boolean
     */
    boolean reserve(String filterKey, float error_rate, int capacity);
}
