package com.usercenter.usercenterbackend.service;

import com.usercenter.usercenterbackend.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setUserAccount("123");
        user.setAvatar("https://www.google.com/images/branding/googlelogo/2x/googlelogo_light_color_272x92dp.png");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhoneNum("123");
        user.setEmail("456");

        boolean result = userService.save(user);
        System.out.println("User ID is: " + user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "Mike";
        String userPassword = "1234567890";
        String checkPassword = "123456789";
        String number = "2";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, number);
        Assertions.assertEquals(-1, result);

        checkPassword = "1234567890";
        result = userService.userRegister(userAccount, userPassword, checkPassword, number);
        Assertions.assertTrue(result < 0);
    }
}