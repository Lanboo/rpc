package com.xych.rpc.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.xych.rpc.common.Invocation;
import com.xych.rpc.common.rpc.RpcInvocation;

public class ProcessorHandler implements Runnable {
    private final Map<String, Object> serviceMap;
    private Socket socket;

    public ProcessorHandler(Map<String, Object> serviceMap, Socket socket) {
        this.serviceMap = serviceMap;
        this.socket = socket;
    }

    @Override
    public void run() {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            Invocation invocation = (RpcInvocation) ois.readObject();
            Object result = invoke(invocation);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(result);
            oos.flush();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(oos);
        }
    }

    private Object invoke(Invocation invocation) {
        Object service = this.serviceMap.get(invocation.getClassName());
        Class<?> serviceClass = service.getClass();
        try {
            Method method = serviceClass.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            Object result = method.invoke(service, invocation.getArguments());
            return result;
        }
        catch(NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
