package com.xych.rpc.common.rpc;

import java.io.Serializable;

import com.xych.rpc.common.Invocation;

import lombok.Data;

@Data
public class RpcInvocation implements Invocation, Serializable {
    private static final long serialVersionUID = 1L;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] arguments;
}
