package com.xych.rpc.client;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import com.xych.rpc.common.rpc.RpcInvocation;
import com.xych.rpc.common.rpc.RpcResult;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcNetTransport {
    private final String host;
    private final int port;

    public RpcNetTransport(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Object send(RpcInvocation rpcInvocation) throws Throwable {
        log.info("RpcInvocation={}", rpcInvocation);
        ClientChannelHandler channelHandler = new ClientChannelHandler(new CountDownLatch(1));
        Bootstrap bootstrap = initBootstrap(channelHandler);
        ChannelFuture future = bootstrap.connect();
        future.channel().writeAndFlush(rpcInvocation);
        future.channel().closeFuture().sync();
        RpcResult rpcResult = channelHandler.getRpcResult();
        if(rpcResult.getThrowable() != null) {
            throw rpcResult.getThrowable();
        }
        else {
            return rpcResult.getResult();
        }
    }

    private Bootstrap initBootstrap(ClientChannelHandler channelHandler) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = null;
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)//
                .channel(NioSocketChannel.class)//
                .option(ChannelOption.TCP_NODELAY, true)//
                .remoteAddress(new InetSocketAddress(host, port))//
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
                        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                        pipeline.addLast("encoder", new ObjectEncoder());
                        pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                        // 业务处理
                        pipeline.addLast("handler", channelHandler);
                    }
                });

        }
        catch(Exception e) {
            log.error("Client初始化错误", e);
        }
        return bootstrap;
    }
}
