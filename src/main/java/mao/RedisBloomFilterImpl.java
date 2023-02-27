package mao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Project name(项目名称)：布隆过滤器
 * Package(包名): mao
 * Class(类名): RedisBloomFilterImpl
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/2/27
 * Time(创建时间)： 22:05
 * Version(版本): 1.0
 * Description(描述)： redis布隆过滤器客户端接口实现类
 */

public class RedisBloomFilterImpl implements RedisBloomFilter
{
    private final Socket socket;
    private final PrintWriter printWriter;
    private final BufferedReader bufferedReader;


    /**
     * Instantiates a new Redis client.
     */
    public RedisBloomFilterImpl()
    {
        try
        {
            //连接redis
            socket = new Socket(RedisInformation.getHost(), RedisInformation.getPort());
            //获取输入流
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            //获取输出流
            printWriter = new PrintWriter(socket.getOutputStream());
            //身份认证
            if (RedisInformation.getPassword() != null)
            {
                sendRequest("auth", RedisInformation.getPassword());
                Object response = getResponse();
                System.out.println("密码验证成功");
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Instantiates a new Redis client.
     *
     * @param host     the host
     * @param port     the port
     * @param password the password
     */
    public RedisBloomFilterImpl(String host, int port, String password)
    {
        try
        {
            //连接redis
            socket = new Socket(host, port);
            //获取输入流
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            //获取输出流
            printWriter = new PrintWriter(socket.getOutputStream());
            //身份认证
            if (password != null)
            {
                sendRequest("auth", password);
                Object response = getResponse();
                System.out.println("密码验证成功：" + response);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Instantiates a new Redis client.
     *
     * @param host the host
     * @param port the port
     */
    public RedisBloomFilterImpl(String host, int port)
    {
        try
        {
            //连接redis
            socket = new Socket(host, port);
            //获取输入流
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            //获取输出流
            printWriter = new PrintWriter(socket.getOutputStream());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    /**
     * 关闭redis客户端与redis服务端的连接
     */
    public void close()
    {
        try
        {
            if (printWriter != null)
            {
                printWriter.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            if (bufferedReader != null)
            {
                bufferedReader.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            if (socket != null)
            {
                socket.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 发送请求
     *
     * @param args 发起请求的参数，参数数量不一定
     */
    private void sendRequest(String... args)
    {
        //先写入元素个数，数组，换行
        printWriter.println("*" + args.length);
        //剩余的都是数组，遍历添加
        for (String arg : args)
        {
            //$为多行字符串，长度
            printWriter.println("$" + arg.getBytes(StandardCharsets.UTF_8).length);
            printWriter.println(arg);
        }
        //刷新
        printWriter.flush();
    }

    /**
     * 获取发送请求后的响应
     *
     * @return Object对象
     */
    private Object getResponse()
    {
        try
        {
            //获取当前前缀，因为要判断是什么类型
            char prefix = (char) bufferedReader.read();
            //判断是什么类型
            if (prefix == RedisInformation.SINGLE_LINE_STRING)
            {
                //单行字符串
                //直接读一行，读到换行符
                return bufferedReader.readLine();
            }
            if (prefix == RedisInformation.ERROR)
            {
                //错误
                //抛出运行时异常
                throw new RuntimeException(bufferedReader.readLine());
            }
            if (prefix == RedisInformation.NUMBER)
            {
                //数值
                //转数字
                return Integer.valueOf(bufferedReader.readLine());
            }
            if (prefix == RedisInformation.MULTILINE_STRING)
            {
                //多行字符串
                //先获取长度
                int length = Integer.parseInt(bufferedReader.readLine());
                //判断数组是否为空
                if (length == -1 || length == 0)
                {
                    //不存在或者数组为空
                    //返回空字符串
                    return "";
                }
                //不为空，读取
                return bufferedReader.readLine();
            }
            if (prefix == RedisInformation.ARRAY)
            {
                //数组
                return readBulkString();
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 读取数组
     *
     * @return List<Object>
     * @throws IOException IOException
     */
    private List<Object> readBulkString() throws IOException
    {
        //获取当前数组的大小
        int size = Integer.parseInt(bufferedReader.readLine());
        //判断数组大小
        if (size == 0 || size == -1)
        {
            //返回null
            return null;
        }
        //数组有值
        //构建集合
        List<Object> list = new ArrayList<>(3);
        //遍历获取
        for (int i = 0; i < size; i++)
        {
            try
            {
                //递归获取
                list.add(getResponse());
            }
            catch (Exception e)
            {
                //异常加入到集合
                list.add(e);
            }
        }
        //返回
        return list;
    }

    /**
     * 将元素添加到布隆过滤器中，如果该过滤器尚不存在，则创建该过滤器。
     *
     * @param filterKey 布隆过滤器的名称
     * @param item      添加的元素
     * @return boolean
     */
    public boolean add(String filterKey, String item)
    {
        //发送命令
        this.sendRequest("BF.ADD", filterKey, item);
        //读取结果
        String response = Objects.requireNonNull(this.getResponse()).toString();
        if (Objects.equals(response, "1"))
        {
            return true;
        }
        if (Objects.equals(response, "0"))
        {
            return false;
        }
        throw new RuntimeException(Objects.requireNonNull(response).toString());
    }


    /**
     * 将一个或多个元素添加到“布隆过滤器”中，并创建一个尚不存在的过滤器。
     * 该命令的操作方式`BF.ADD`与之相同，只不过它允许多个输入并返回多个值。
     *
     * @param filterKey 布隆过滤器的名称
     * @param items     添加的元素
     * @return {@link List}<{@link Boolean}>
     */
    public List<Boolean> mAdd(String filterKey, String... items)
    {
        if (filterKey == null)
        {
            return null;
        }

        String[] args = new String[items.length + 2];
        args[0] = "BF.MADD";
        args[1] = filterKey;
        System.arraycopy(items, 0, args, 2, items.length);
        sendRequest(args);
        String r = getResponse().toString();
        r = r.substring(1, r.length() - 1);
        String[] split = r.split(", ");
        List<Boolean> list = new ArrayList<>(items.length);
        for (String s : split)
        {
            if (Objects.equals(s, "1"))
            {
                list.add(true);
                continue;
            }
            if (Objects.equals(s, "0"))
            {
                list.add(false);
                continue;
            }
            throw new RuntimeException(Objects.requireNonNull(s).toString());
        }
        return list;
    }


    public static void main(String[] args)
    {
        RedisBloomFilterImpl bloomFilter = new RedisBloomFilterImpl("127.0.0.1", 16379);
        bloomFilter.sendRequest("BF.ADD", "filter", "1");
        System.out.println(bloomFilter.getResponse());
        bloomFilter.sendRequest("BF.MADD", "filter", "1", "2", "3");
        String s = bloomFilter.getResponse().toString();
        s = s.substring(1, s.length() - 1);
        System.out.println(s);
        String[] split = s.split(", ");
        for (String s1 : split)
        {
            System.out.println(s1);
        }
    }
}
