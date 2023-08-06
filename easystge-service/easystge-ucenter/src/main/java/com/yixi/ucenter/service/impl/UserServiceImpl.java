package com.yixi.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixi.common.constants.EmailConstant;
import com.yixi.common.constants.UserConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.EventCode;
import com.yixi.ucenter.model.entity.User;
import com.yixi.ucenter.model.vo.UserRegistVo;
import com.yixi.ucenter.service.UserService;
import com.yixi.ucenter.mapper.UserMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author yixi
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-08-05 22:05:19
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    final private StringRedisTemplate redisTemplate;


    public UserServiceImpl(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }



    /**
     * 用户注册
     * @param userVo
     */
    @Override
    public String register(UserRegistVo userVo) throws BusinessException {
        // 获取注册的数据
        String emailVC = userVo.getEmailVC();       //邮箱验证码
        String email = userVo.getEmail();           //邮箱号码
        String password = userVo.getPassword();     //密码
        String verifyCode = userVo.getVerifyCode(); //验证码

        //校验-------------------------------------------------------------------
            // 非空判断
        if ( !StringUtils.hasLength(emailVC) || !StringUtils.hasLength(email)
                || !StringUtils.hasLength(password) || !StringUtils.hasLength(verifyCode) ){
            throw new BusinessException(EventCode.PARAMS_ERROR,"注册失败");
        }

            //邮箱格式校验
        String emailRegex = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        Matcher matcher = Pattern.compile(emailRegex).matcher(email);
        if (!matcher.matches()){
            throw new BusinessException(EventCode.PARAMS_ERROR,"邮箱格式错误");
        }

            // 判断邮箱验证码

        String vcKey = EmailConstant.EMAIL_VC_KEY+email;    // redis保存验证码的 key
        String redisEmailVC = redisTemplate.opsForValue().get(vcKey);
        if (!StringUtils.hasLength(redisEmailVC)){
            throw new BusinessException(EventCode.PARAMS_ERROR,"邮箱验证码已过期");
        }
        redisEmailVC = redisEmailVC.split("_")[0];  // 之前保存邮箱验证码的时候有在其后面用_拼接系统时间，需要截取
        if (!emailVC.equals(redisEmailVC)){
            throw new BusinessException(EventCode.PARAMS_ERROR,"邮箱验证码错误");
        }
        redisTemplate.delete(vcKey); // 删除redis中邮箱验证码


            // 判断邮箱号码是否重复
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        long count = baseMapper.selectCount(wrapper);
        if (count > 0){     // 说明数据库表中有相同的邮箱号码
            throw new BusinessException(EventCode.USER_EXIST_EXCEPTION,"邮箱已被注册");
        }

        // 数据插入到数据库中
        User user = new User();
        user.setEmail(email);
        user.setPassword(new BCryptPasswordEncoder().encode(password));  // 对密码进行加密保存
        user.setUseSpace(0L);
        user.setTotalSpace(UserConstant.USER_INIT_TOTAL_SPACE);
        baseMapper.insert(user);

        // 返回用户id
        return user.getUserId();
    }


}




