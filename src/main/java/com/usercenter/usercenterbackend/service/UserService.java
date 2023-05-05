package com.usercenter.usercenterbackend.service;

import com.usercenter.usercenterbackend.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author zhengyangwang
* @description 针对表【user】的数据库操作Service
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

    User userLogin(String userAccount, String userPassword, HttpServletRequest request);
}
