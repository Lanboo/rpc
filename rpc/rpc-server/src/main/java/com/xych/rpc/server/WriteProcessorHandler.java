package com.xych.rpc.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.commons.io.IOUtils;

public class WriteProcessorHandler implements Runnable {

    private SelectionKey writeKey;

    public WriteProcessorHandler(SelectionKey writeKey) {
        this.writeKey = writeKey;
    }

    @Override
    public void run() {
        SocketChannel channel = null;
        ObjectOutputStream oos = null;
        try {
            channel = (SocketChannel) this.writeKey.channel();
            Object result = this.writeKey.attachment();
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
        }
    }
}
