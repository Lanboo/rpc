package com.xych.rpc.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
        try(Socket socket = new Socket(host, port);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());) {
            oos.writeObject(rpcInvocation);
            oos.flush();
            result = ois.readObject();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
