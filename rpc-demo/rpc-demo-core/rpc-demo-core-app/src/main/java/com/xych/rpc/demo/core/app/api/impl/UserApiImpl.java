package com.xych.rpc.demo.core.app.api.impl;

import com.xych.rpc.demo.core.api.api.UserApi;
import com.xych.rpc.demo.core.api.request.UserRequest;

public class UserApiImpl implements UserApi {

    @Override
    public Integer save(UserRequest user) {
        System.out.println("save " + user);
        return 1;
    }

}
