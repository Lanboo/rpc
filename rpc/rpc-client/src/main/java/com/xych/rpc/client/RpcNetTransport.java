package com.xych.rpc.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.commons.io.IOUtils;

import com.xych.rpc.common.Result;
import com.xych.rpc.common.rpc.RpcInvocation;
import com.xych.rpc.common.rpc.RpcResult;

public class RpcNetTransport {
    private InetSocketAddress serverAddr;

    public RpcNetTransport(String host, int port) {
        this.serverAddr = new InetSocketAddress(host, port);
    }

    public Object send(RpcInvocation rpcInvocation) throws Throwable {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        ObjectInputStream ois = null;
        SocketChannel channel = null;
        try {
            channel = SocketChannel.open(serverAddr);
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(rpcInvocation);
            //            oos.flush();
            byte[] rpcMsg = baos.toByteArray();
            ByteBuffer writeBuffer = ByteBuffer.wrap(rpcMsg);
            channel.write(writeBuffer);
            //
            ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);
            if(channel.read(readBuffer) > 0) {
                byte[] resultMsg = readBuffer.array();
                if(resultMsg != null && resultMsg.length > 0) {
                    ois = new ObjectInputStream(new ByteArrayInputStream(resultMsg));
                    Result rpcResult = (RpcResult) ois.readObject();
                    if(rpcResult.getThrowable() != null) {
                        throw rpcResult.getThrowable();
                    }
                    else {
                        return rpcResult.getResult();
                    }
                }
            }
            throw new RuntimeException("no receive msg");
        }
        catch(IOException | ClassNotFoundException e) {
            throw e;
        }
        finally {
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(channel);
        }
    }
}
