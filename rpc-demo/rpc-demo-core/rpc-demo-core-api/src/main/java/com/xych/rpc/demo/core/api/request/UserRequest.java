package com.xych.rpc.demo.core.api.request;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Integer age;
    private String sex;
}
