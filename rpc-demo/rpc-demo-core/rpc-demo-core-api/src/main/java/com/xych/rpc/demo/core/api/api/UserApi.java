package com.xych.rpc.demo.core.api.api;

import com.xych.rpc.demo.core.api.request.UserRequest;

public interface UserApi {
    Integer save(UserRequest user);
}
