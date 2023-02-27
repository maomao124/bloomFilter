package mao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Project name(项目名称)：布隆过滤器
 * Package(包名): mao
 * Class(类名): RedisClient
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/2/27
 * Time(创建时间)： 22:02
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class RedisClient
{
    private final Socket socket;
    private final PrintWriter printWriter;
    private final BufferedReader bufferedReader;


    /**
     * Instantiates a new Redis client.
     */
    public RedisClient()
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
    public RedisClient(String host, int port, String password)
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
    public RedisClient(String host, int port)
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
     * redis的get命令
     *
     * @param key key
     * @return value
     */
    public Object get(String key)
    {
        if (key == null)
        {
            return null;
        }
        sendRequest("get", key);
        Object response = getResponse();
        if (response == null || response.equals(""))
        {
            return null;
        }
        return response;
    }

    /**
     * redis的set命令
     *
     * @param key   key
     * @param value value
     * @return Object
     */
    public Object set(String key, String value)
    {
        if (key == null)
        {
            return null;
        }
        sendRequest("set", key, value);
        return getResponse();
    }

    /**
     * redis的del命令，删除一个key
     *
     * @param key key
     * @return Object(删除成功的个数)
     */
    public Object delete(String key)
    {
        if (key == null)
        {
            return null;
        }
        sendRequest("del", key);
        return getResponse();
    }

    /**
     * redis的mget命令
     *
     * @param key key
     * @return Object(list集合)
     */
    public Object mget(String... key)
    {
        if (key == null || key.length == 0)
        {
            return null;
        }
        String[] args = new String[key.length + 1];
        args[0] = "mget";
        System.arraycopy(key, 0, args, 1, key.length);
        sendRequest(args);
        return getResponse();
    }


    /**
     * redis的mset命令
     *
     * @param key_and_value 一个key，一个value，一个key，一个value......
     * @return Object
     */
    public Object mset(String... key_and_value)
    {
        if (key_and_value == null || key_and_value.length == 0)
        {
            return null;
        }
        String[] args = new String[key_and_value.length + 1];
        args[0] = "mset";
        System.arraycopy(key_and_value, 0, args, 1, key_and_value.length);
        sendRequest(args);
        return getResponse();
    }

    /**
     * 设置key的过期时间
     *
     * @param key          key
     * @param milliseconds 毫秒数
     * @return Object
     */
    public Object pexpire(String key, Long milliseconds)
    {
        if (key == null)
        {
            return null;
        }
        sendRequest("PEXPIRE", key, milliseconds.toString());
        return getResponse();
    }

    /**
     * 查看key的过期时间
     *
     * @param key key
     * @return Object ，过期时间，单位为秒
     */
    public Object ttl(String key)
    {
        if (key == null)
        {
            return null;
        }
        sendRequest("TTL", key);
        return getResponse();
    }

    /**
     * hset命令
     *
     * @param key             key
     * @param field_and_value field和value，例如：a 1 b 2 c 3
     * @return Object
     */
    public Object hset(String key, String... field_and_value)
    {
        if (key == null)
        {
            return null;
        }
        String[] args = new String[field_and_value.length + 2];
        args[0] = "hset";
        args[1] = key;
        System.arraycopy(field_and_value, 0, args, 2, field_and_value.length);
        sendRequest(args);
        return getResponse();
    }

    /**
     * hget命令
     *
     * @param key   key
     * @param field field
     * @return Object(value)
     */
    public Object hget(String key, String field)
    {
        if (key == null)
        {
            return null;
        }
        sendRequest("hget", key, field);
        Object response = getResponse();
        if (response == null || response.equals(""))
        {
            return null;
        }
        return response;
    }


    /**
     * 获取存储在 key 中的哈希表的所有字段
     *
     * @param key key
     * @return Object (list集合）
     */
    public Object hkeys(String key)
    {
        if (key == null)
        {
            return null;
        }
        sendRequest("hkeys", key);
        return getResponse();
    }

    /**
     * 用于获取哈希表中的所有值
     *
     * @return Object (list集合）
     */
    public Object hvals(String key)
    {
        if (key == null)
        {
            return null;
        }
        sendRequest("hvals", key);
        return getResponse();
    }

    /**
     * 获取存储在 key 中的哈希表的字段数量
     *
     * @param key key
     * @return Object（字段数量）
     */
    public Object hlen(String key)
    {
        if (key == null)
        {
            return null;
        }
        sendRequest("hlen", key);
        return getResponse();
    }

    /**
     * 在列表头部插入一个或者多个值
     *
     * @param key   key
     * @param value value
     * @return Object
     */
    public Object lpush(String key, String... value)
    {
        if (key == null)
        {
            return null;
        }

        String[] args = new String[value.length + 2];
        args[0] = "lpush";
        args[1] = key;
        System.arraycopy(value, 0, args, 2, value.length);
        sendRequest(args);
        return getResponse();
    }

    /**
     * 获取列表的长度
     *
     * @param key key
     * @return Object（字段数量）
     */
    public Object llen(String key)
    {
        if (key == null)
        {
            return null;
        }
        sendRequest("llen", key);
        return getResponse();
    }

    /**
     * 从列表的头部弹出元素，默认为第一个元素
     *
     * @param key key
     * @return Object（弹出的元素）
     */
    public Object lpop(String key)
    {
        if (key == null)
        {
            return null;
        }
        sendRequest("lpop", key);
        Object response = getResponse();
        if (response == null || response.equals(""))
        {
            return null;
        }
        return response;
    }


    /**
     * 向集合中添加一个或者多个元素，并且自动去重
     *
     * @param key    key
     * @param member member
     * @return Object
     */
    public Object sadd(String key, String... member)
    {
        if (key == null)
        {
            return null;
        }

        String[] args = new String[member.length + 2];
        args[0] = "sadd";
        args[1] = key;
        System.arraycopy(member, 0, args, 2, member.length);
        sendRequest(args);
        return getResponse();
    }


    /**
     * 弹出指定数量的元素
     *
     * @param key   key
     * @param count 要弹出的元素的数量
     * @return Object
     */
    public Object spop(String key, int count)
    {
        if (key == null)
        {
            return null;
        }
        sendRequest("spop", key, String.valueOf(count));
        return getResponse();
    }


    /**
     * Test.
     */
    public void test()
    {
        /*sendRequest("get", "key11");
        Object response = getResponse();
        System.out.println(response);*/
        System.out.println(get("key11"));
        System.out.println(get("key11"));
        System.out.println(get("key1"));
        System.out.println(set("key12", "125678656"));
        System.out.println(get("key12"));
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args)
    {
        new RedisClient().test();
    }
}
