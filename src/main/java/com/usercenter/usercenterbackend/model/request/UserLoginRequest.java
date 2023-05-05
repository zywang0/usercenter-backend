package com.usercenter.usercenterbackend.model.request;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 4238927209117279669L;
    private String userAccount;
    private String userPassword;
}
