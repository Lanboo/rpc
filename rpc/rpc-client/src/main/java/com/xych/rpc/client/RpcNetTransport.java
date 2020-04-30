package com.xych.rpc.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

import com.xych.rpc.common.rpc.RpcInvocation;

public class RpcNetTransport {
    private String host;
    private int port;

    public RpcNetTransport(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    public Object send(RpcInvocation rpcInvocation) {
        Object result = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try(Socket socket = new Socket(host, port)) {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(rpcInvocation);
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            result = ois.readObject();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(ois);
        }
        return result;
    }
}
