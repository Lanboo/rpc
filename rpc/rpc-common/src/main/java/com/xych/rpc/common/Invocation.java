package com.xych.rpc.common;

public interface Invocation {
    String getClassName();

    String getMethodName();

    Class<?>[] getParameterTypes();

    Object[] getArguments();
}
