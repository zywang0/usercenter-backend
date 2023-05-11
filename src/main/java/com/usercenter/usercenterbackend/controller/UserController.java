package com.usercenter.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.usercenter.usercenterbackend.model.User;
import com.usercenter.usercenterbackend.model.request.UserLoginRequest;
import com.usercenter.usercenterbackend.model.request.UserRegisterRequest;
import com.usercenter.usercenterbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.usercenter.usercenterbackend.constant.UserConstant.ADMIN_ROLE;
import static com.usercenter.usercenterbackend.constant.UserConstant.LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) return null;
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) return null;
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null) return null;
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) return null;
        return userService.userLogin(userAccount, userPassword, request);
    }

    @GetMapping("/current")
    public User getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) return null;
        long id = currentUser.getId();
        User user = userService.getById(id);
        return userService.getInsensitiveUser(user);
    }

    @GetMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {
        if (notAdmin(request)) return new ArrayList<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) queryWrapper.like("username", username);
        List<User> userList = userService.list(queryWrapper);
        return userList.stream().map(user -> userService.getInsensitiveUser(user)).collect(Collectors.toList());
    }

    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id, HttpServletRequest request){
        if (notAdmin(request)) return false;
        if (id <= 0) return false;
        return userService.removeById(id);
    }

    /**
     * Only if role is Admin
     * @return boolean
     */
    private static boolean notAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(LOGIN_STATE);
        User user = (User) userObj;
        return user == null || user.getRole() != ADMIN_ROLE;
    }
}
