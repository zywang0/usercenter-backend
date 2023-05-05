package com.usercenter.usercenterbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usercenter.usercenterbackend.model.User;
import com.usercenter.usercenterbackend.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    @Resource
    private UserMapper userMapper;
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //Verification
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            return -1;
        }
        if(userAccount.length() < 4){
            return -1;
        }
        if(userPassword.length() < 8 || checkPassword.length() < 8){
            return -1;
        }

        //userAccount can't contain special characters
        String validPatter = "[\\\\u00A0\\\\s\\\"`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPatter).matcher(userAccount);
        if(matcher.find()) return -1;

        //Password and confirmation password must be the same
        if(!userPassword.equals(checkPassword)){
            return -1;
        }

        //userAccount can't duplicate
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0) return -1;

        //Encrypt user password
        final String SALT = "password";
        String encryption = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //insert user info into db
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryption);
        boolean saveResult = this.save(user);
        if(!saveResult) return -1;
        return user.getId();
    }
}




