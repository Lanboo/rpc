package com.xych.rpc.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriteProcessorHandler implements Runnable {

    private SelectionKey writeKey;
    private final CountDownLatch downLatch;

    public WriteProcessorHandler(SelectionKey writeKey, CountDownLatch downLatch) {
        this.writeKey = writeKey;
        this.downLatch = downLatch;
    }

    @Override
    public void run() {
        log.info("SelectionKey.OP_WRITE Start");
        SocketChannel channel = null;
        ObjectOutputStream oos = null;
        try {
            channel = (SocketChannel) this.writeKey.channel();
            Object result = this.writeKey.attachment();
            log.info("OP_WRITE:RpcResult={}", result);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(result);
            oos.flush();
            byte[] resultMsg = baos.toByteArray();
            ByteBuffer writeBuffer = ByteBuffer.wrap(resultMsg);
            channel.write(writeBuffer);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(channel);
            this.downLatch.countDown();
        }
        log.info("SelectionKey.OP_WRITE End");
    }
}
