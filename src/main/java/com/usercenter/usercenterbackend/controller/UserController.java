package com.usercenter.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.usercenter.usercenterbackend.common.BaseResponse;
import com.usercenter.usercenterbackend.common.ErrorCode;
import com.usercenter.usercenterbackend.common.ResultUtils;
import com.usercenter.usercenterbackend.exception.BusinessException;
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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String number = userRegisterRequest.getNumber();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, number)) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        long userRegister = userService.userRegister(userAccount, userPassword, checkPassword, number);
        return ResultUtils.ok(userRegister);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.ok(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        int userLogout = userService.userLogout(request);
        return ResultUtils.ok(userLogout);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) throw new BusinessException(ErrorCode.NOT_LOGIN);
        long id = currentUser.getId();
        User user = userService.getById(id);
        User insensitiveUser = userService.getInsensitiveUser(user);
        return ResultUtils.ok(insensitiveUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (notAdmin(request)) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) queryWrapper.like("username", username);
        List<User> userList = userService.list(queryWrapper);
        List<User> users = userList.stream().map(user -> userService.getInsensitiveUser(user)).collect(Collectors.toList());
        return ResultUtils.ok(users);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request){
        if (notAdmin(request)) throw new BusinessException(ErrorCode.NO_AUTH);
        if (id <= 0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        boolean removeById = userService.removeById(id);
        return ResultUtils.ok(removeById);
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
