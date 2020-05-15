package com.xych.rpc.client;

import java.net.InetSocketAddress;

import com.xych.rpc.common.rpc.RpcInvocation;
import com.xych.rpc.common.rpc.RpcResult;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
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
        ClientChannelHandler channelHandler = new ClientChannelHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = null;
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)//
                .channel(NioSocketChannel.class)//
                .option(ChannelOption.TCP_NODELAY, true)//
                .remoteAddress(new InetSocketAddress(host, port))//
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //自定义协议解码器
                        /**
                         * 入参有5个，分别解释如下
                         * maxFrameLength：框架的最大长度。如果帧的长度大于此值，则将抛出TooLongFrameException。
                         * lengthFieldOffset：长度字段的偏移量：即对应的长度字段在整个消息数据中得位置
                         * lengthFieldLength：长度字段的长度。如：长度字段是int型表示，那么这个值就是4（long型就是8）
                         * lengthAdjustment：要添加到长度字段值的补偿值
                         * initialBytesToStrip：从解码帧中去除的第一个字节数
                         */
                        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        //自定义协议编码器
                        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                        //对象参数类型编码器
                        pipeline.addLast("encoder", new ObjectEncoder());
                        //对象参数类型解码器
                        pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                        // 业务处理
                        pipeline.addLast("handler", channelHandler);
                    }
                });
            ChannelFuture future = bootstrap.connect().sync();
            future.channel().writeAndFlush(rpcInvocation).sync();
            future.channel().closeFuture().sync();

        }
        catch(Exception e) {
            log.error("Client初始化错误", e);
        }
        finally {
            group.shutdownGracefully();
        }
        RpcResult rpcResult = channelHandler.getRpcResult();
        if(rpcResult.getThrowable() != null) {
            throw rpcResult.getThrowable();
        }
        else {
            return rpcResult.getResult();
        }
    }

}
