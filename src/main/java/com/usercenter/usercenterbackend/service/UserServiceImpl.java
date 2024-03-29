package com.usercenter.usercenterbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usercenter.usercenterbackend.common.ErrorCode;
import com.usercenter.usercenterbackend.exception.BusinessException;
import com.usercenter.usercenterbackend.model.User;
import com.usercenter.usercenterbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.usercenter.usercenterbackend.constant.UserConstant.LOGIN_STATE;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    private static final String SALT = "password";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String number) {
        //Verification
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, number))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        if (userAccount.length() < 4) throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        if (userPassword.length() < 8 || checkPassword.length() < 8)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        if (number.length() > 5) throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号过长");

        //userAccount can't contain special characters
        String validPatter = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPatter).matcher(userAccount);
        if (matcher.find()) throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名密码不匹配");

        //Password and confirmation password must be the same
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        //userAccount can't duplicate
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");

        //user number can't duplicate
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("number", number);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号重复");

        //Encrypt user password
        String encryption = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //insert user info into db
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryption);
        user.setNumber(number);
        boolean saveResult = this.save(user);
        if (!saveResult) return -1;
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //Verification
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }

        //userAccount can't contain special characters
        String validPatter = "[\\\\u00A0\\\\s\\\"`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPatter).matcher(userAccount);
        if (matcher.find()) return null;

        //Encrypt user password
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //Validate username matches password
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("User login failed because username can't match password.");
            return null;
        }

        //desensitization
        User insensitiveUser = getInsensitiveUser(user);

        //save user login status
        request.getSession().setAttribute(LOGIN_STATE, insensitiveUser);

        return insensitiveUser;
    }

    /**
     * desensitization to return user information to frontend
     *
     * @param user
     * @return
     */
    @Override
    public User getInsensitiveUser(User user) {
        if (user == null) return null;
        User insensitiveUser = new User();
        insensitiveUser.setId(user.getId());
        insensitiveUser.setUsername(user.getUsername());
        insensitiveUser.setUserAccount(user.getUserAccount());
        insensitiveUser.setAvatar(user.getAvatar());
        insensitiveUser.setGender(user.getGender());
        insensitiveUser.setPhoneNum(user.getPhoneNum());
        insensitiveUser.setEmail(user.getEmail());
        insensitiveUser.setUserStatus(user.getUserStatus());
        insensitiveUser.setCreateTime(user.getCreateTime());
        insensitiveUser.setRole(user.getRole());
        insensitiveUser.setNumber(user.getNumber());
        return insensitiveUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(LOGIN_STATE);
        return 1;
    }
}




