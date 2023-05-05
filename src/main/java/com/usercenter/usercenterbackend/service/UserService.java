package com.usercenter.usercenterbackend.service;

import com.usercenter.usercenterbackend.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author zhengyangwang
* @description 针对表【user】的数据库操作Service
* @createDate 2023-03-12 00:17:50
*/
public interface UserService extends IService<User> {

    /**
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);
}
