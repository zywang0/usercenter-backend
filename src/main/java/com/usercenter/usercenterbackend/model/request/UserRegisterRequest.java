package com.usercenter.usercenterbackend.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -8388749403068132501L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
