package com.xych.rpc.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.xych.rpc.common.Invocation;
import com.xych.rpc.common.rpc.RpcInvocation;
import com.xych.rpc.common.rpc.RpcResult;

public class ReadProcessorHandler implements Runnable {
    private final Map<String, Object> serviceMap;
    private final Selector selector;
    private final SelectionKey readKey;

    public ReadProcessorHandler(Map<String, Object> serviceMap, Selector selector, SelectionKey readKey) {
        this.serviceMap = serviceMap;
        this.selector = selector;
        this.readKey = readKey;
    }

    @Override
    public void run() {
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        SocketChannel channel = (SocketChannel) this.readKey.channel();
        ObjectInputStream ois = null;
        try {
            /**
             * <pre>
             * channel.read 返回值
             * https://blog.csdn.net/p19777/article/details/92781054
             * 返回-1是因为客户端主动关闭了channel，注意是主动关闭而不是异常关闭。
             * 返回值为0的情况：
             *     byteBuffer已经存满了，会返回0
             *     channel中其实并没有数据可读，在我们迭代的时候没有删除该SelectionKey可能会出现此种情况。
             *     网卡资源被其他socket占用了
             * 大于0的情况，就是正常的读取数据的长度。
             * </pre>
             */
            int readFlag = channel.read(readBuffer);
            if(readFlag > 0) {
                byte[] rpcMsg = readBuffer.array();
                if(rpcMsg != null && rpcMsg.length > 0) {
                    ois = new ObjectInputStream(new ByteArrayInputStream(rpcMsg));
                    RpcInvocation invocation = (RpcInvocation) ois.readObject();
                    RpcResult result = invoke(invocation);
                    channel.register(this.selector, SelectionKey.OP_WRITE, result);
                }
            }
            else if(readFlag == -1) {
                channel.close();
            }
        }
        catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(ois);
        }
    }

    private RpcResult invoke(Invocation invocation) {
        RpcResult rpcResult = new RpcResult();
        try {
            Object service = this.serviceMap.get(invocation.getClassName());
            Class<?> serviceClass = service.getClass();
            Method method = serviceClass.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            Object result = method.invoke(service, invocation.getArguments());
            rpcResult.setResult(result);
        }
        catch(Throwable e) {
            rpcResult.setThrowable(e);
        }
        return rpcResult;
    }
}
