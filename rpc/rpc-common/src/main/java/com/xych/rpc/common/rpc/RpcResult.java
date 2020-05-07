package com.xych.rpc.common.rpc;

import java.io.Serializable;

import com.xych.rpc.common.Result;

import lombok.Data;

@Data
public class RpcResult implements Result, Serializable {
    private static final long serialVersionUID = 1L;
    private Object result;
    private Throwable throwable;
}
