package com.xych.rpc.server;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Map;

import com.xych.rpc.common.Invocation;
import com.xych.rpc.common.rpc.RpcInvocation;
import com.xych.rpc.common.rpc.RpcResult;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {
    private final Map<String, Object> serviceMap;

    public ServerChannelHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(log.isInfoEnabled()) {
            InetSocketAddress remoteAddr = (InetSocketAddress) ctx.channel().remoteAddress();
            log.info("客户端发起连接:remote.ip={},remote.port={}", remoteAddr.getHostString(), remoteAddr.getPort());

            InetSocketAddress localAddr = (InetSocketAddress) ctx.channel().localAddress();
            log.info("客户端发起连接:local.ip={},local.port={}", localAddr.getHostString(), localAddr.getPort());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("READ:msg={}", msg);
        RpcResult result = null;
        if(msg != null && msg instanceof RpcInvocation) {
            result = invoke((RpcInvocation) msg);
        }
        else {
            result = new RpcResult();
            result.setThrowable(new RuntimeException("消息有误" + msg));
        }
        log.info("READ:RpcResult={}", result);
        ctx.channel().writeAndFlush(result);
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
