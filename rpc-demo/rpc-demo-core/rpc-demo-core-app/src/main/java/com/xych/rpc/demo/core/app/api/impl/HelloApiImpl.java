package com.xych.rpc.demo.core.app.api.impl;

import com.xych.rpc.demo.core.api.api.HelloApi;

public class HelloApiImpl implements HelloApi {

    @Override
    public void sayHello() {
        say("Hello Ward!");
    }

    @Override
    public void say(String msg) {
        System.out.println(msg);
    }

}
