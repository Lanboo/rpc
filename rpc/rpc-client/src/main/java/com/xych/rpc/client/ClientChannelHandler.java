package com.xych.rpc.client;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import com.xych.rpc.common.rpc.RpcResult;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {
    private CountDownLatch countDownLatch;
    private RpcResult rpcResult;

    public ClientChannelHandler() {
        this.countDownLatch = new CountDownLatch(1);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(log.isInfoEnabled()) {
            InetSocketAddress remoteAddr = (InetSocketAddress) ctx.channel().remoteAddress();
            log.info("客户端发起连接:remote.ip={},remote.port={}", remoteAddr.getHostString(), remoteAddr.getPort());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("Client receive msg=" + msg);
        this.rpcResult = (RpcResult) msg;
        countDownLatch.countDown();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("Client Throwable:" + cause);
        countDownLatch.countDown();
    }

    public RpcResult getRpcResult() {
        try {
            countDownLatch.await();
        }
        catch(InterruptedException e) {
            log.error("", e);
        }
        return rpcResult;
    }
}
