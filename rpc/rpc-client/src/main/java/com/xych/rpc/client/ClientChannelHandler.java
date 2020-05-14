package com.xych.rpc.client;

import java.util.concurrent.CountDownLatch;

import com.xych.rpc.common.rpc.RpcResult;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {
    private CountDownLatch countDownLatch;
    private RpcResult rpcResult;

    public ClientChannelHandler(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Client receive msg=" + msg);
        this.rpcResult = (RpcResult) msg;
        countDownLatch.countDown();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Client Throwable:" + cause);
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
