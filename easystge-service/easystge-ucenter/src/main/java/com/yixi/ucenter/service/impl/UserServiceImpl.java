package com.yixi.ucenter.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixi.common.constants.EmailConstant;
import com.yixi.common.constants.MConstant;
import com.yixi.common.constants.SecurityConstant;
import com.yixi.common.constants.UserConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.JwtUtils;
import com.yixi.ucenter.mapper.UserLogMapper;
import com.yixi.ucenter.model.entity.User;
import com.yixi.ucenter.model.entity.UserLog;
import com.yixi.ucenter.model.vo.UserLoginVo;
import com.yixi.ucenter.model.vo.UserRegistVo;
import com.yixi.ucenter.model.vo.UserSpaceVo;
import com.yixi.ucenter.service.UserService;
import com.yixi.ucenter.mapper.UserMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author yixi
* @description 针对表【user】的数据库操作Service实现
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    final private RedisTemplate<String,Object> redisTemplate;
    final private UserLogMapper userLogMapper;


    public UserServiceImpl(RedisTemplate<String,Object> redisTemplate, UserLogMapper userLogMapper){
        this.redisTemplate = redisTemplate;
        this.userLogMapper = userLogMapper;
    }


    /**
     * 用户登录
     * @param userLoginVo
     * @return
     */
    @Override
    public Map login(UserLoginVo userLoginVo) {
        System.out.println(userLoginVo);
        //获取登录数据
        String lEmail = userLoginVo.getEmail();             //登录邮箱
        String lPassword = userLoginVo.getPassword();       //密码
        String verifyCode = userLoginVo.getVerifyCode();    //验证码
        String vcKey = userLoginVo.getVcKey();              //验证码key

        //校验-------------------------------------------------------------------
            //非空校验
        if (!(StringUtils.hasLength(lEmail) && StringUtils.hasLength(lPassword)
                && StringUtils.hasLength(verifyCode) && StringUtils.hasLength(vcKey))){
            throw new BusinessException(EventCode.NULL_ERROR,"登录失败");
        }

            // 判断图像验证码
        String redisVC = (String) (redisTemplate.opsForValue().get(vcKey));
        if (!(StringUtils.hasLength(redisVC))){
            throw new BusinessException(EventCode.PARAMS_ERROR,"验证码已过期");
        }
        if (!verifyCode.equals(redisVC)){
            throw new BusinessException(EventCode.PARAMS_ERROR,"验证码错误");
        }
        redisTemplate.delete(vcKey); // 删除redis中图像验证码

            //邮箱格式校验
        String emailRegex = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        Matcher matcher = Pattern.compile(emailRegex).matcher(lEmail);
        if (!matcher.matches()){
            throw new BusinessException(EventCode.PARAMS_ERROR,"邮箱错误");
        }


        //从数据库中查询用户信息并进行校验
            // 根据用户邮箱查询用户信息
        User user = null;
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", lEmail);
        try {
            user = baseMapper.selectOne(wrapper);
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(EventCode.SYSTEM_ERROR,"登录服务错误");
        }

            // 判断用户是否存在   密码是否正确
        if (user == null ||  !(new BCryptPasswordEncoder().matches(lPassword, user.getPassword()))){
            throw new BusinessException(EventCode.LOGIN_FAIL,"邮箱或密码错误");
        }

            //判断账户是否被冻结
        if (user.getIsDeleted() == 1){
            throw new BusinessException(EventCode.ACCOUNT_EXCEPTION,"该账户已被冻结");
        }

        // 更新用户的信息 —— 最后一次登录时间
        User updateUser = new User();
        updateUser.setUserId(user.getUserId());
        updateUser.setLastLoginTime(new Date());
        int updateRes = 0;
        try {
            updateRes = baseMapper.updateById(updateUser);
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(EventCode.SYSTEM_ERROR,"登录服务错误");
        }
        if (updateRes != 1){
            throw new BusinessException(EventCode.SYSTEM_ERROR,"登录服务错误");
        }

        // 新增用户日志（登录）
        UserLog userLog = new UserLog();
        userLog.setUserId(user.getUserId());
        userLog.setLogEvent("用户登录");
        userLog.setLogDesc("邮箱密码登录");
        userLog.setLogTime(new Date());
        int insertRes = 0;
        try {
            insertRes = userLogMapper.insert(userLog);
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(EventCode.SYSTEM_ERROR,"登录服务错误");
        }
        if (insertRes != 1){
            throw new BusinessException(EventCode.SYSTEM_ERROR,"登录服务错误");
        }

        // 将用户信息存储到redis中，默认存1天
            //用户信息脱敏，隐藏敏感信息
        User safetyUser = getSafetyUser(user);
        try {
            redisTemplate.opsForValue().set(UserConstant.USER_LOGIN_INFO+user.getUserId(),
                    JSONUtil.toJsonStr(safetyUser),
                    1,
                    TimeUnit.DAYS);
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(EventCode.SYSTEM_ERROR,"登录服务错误");
        }

        // 根据用户id和邮箱生成token
        String token = JwtUtils.getJwtToken(user.getUserId(), user.getEmail());
        // token 拼接前缀
        token = SecurityConstant.TOKEN_PREFIX + token;

        // 封装用户登录信息，身份令牌token
        Map<String,Object> result = new HashMap<>();
        result.put("userInfo", safetyUser);
        result.put("loginToken", token);

        return result;
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
        String vcKey = userVo.getVcKey();           //验证码key

        //校验-------------------------------------------------------------------
            // 非空判断
        if ( !(StringUtils.hasLength(emailVC) && StringUtils.hasLength(email)
                && StringUtils.hasLength(password) && StringUtils.hasLength(verifyCode)
                && StringUtils.hasLength(vcKey))){
            throw new BusinessException(EventCode.NULL_ERROR,"注册失败");
        }

            // 判断图像验证码
        String redisVC = (String) (redisTemplate.opsForValue().get(vcKey));
        if (!StringUtils.hasLength(redisVC)){
            throw new BusinessException(EventCode.PARAMS_ERROR,"验证码已过期");
        }
        if (!verifyCode.equals(redisVC)){
            throw new BusinessException(EventCode.PARAMS_ERROR,"验证码错误");
        }
        redisTemplate.delete(vcKey); // 删除redis中图像验证码



        //邮箱格式校验
        String emailRegex = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        Matcher matcher = Pattern.compile(emailRegex).matcher(email);
        if (!matcher.matches()){
            throw new BusinessException(EventCode.PARAMS_ERROR,"邮箱格式错误");
        }

            // 判断邮箱验证码

        String emailVCKey = EmailConstant.EMAIL_VC_KEY+email;    // redis保存邮箱验证码的 key
        String redisEmailVC = (String)(redisTemplate.opsForValue().get(emailVCKey));
        if (!StringUtils.hasLength(redisEmailVC)){
            throw new BusinessException(EventCode.PARAMS_ERROR,"邮箱验证码已过期");
        }
        redisEmailVC = redisEmailVC.split("_")[0];  // 之前保存邮箱验证码的时候有在其后面用_拼接系统时间，需要截取
        if (!emailVC.equals(redisEmailVC)){
            throw new BusinessException(EventCode.PARAMS_ERROR,"邮箱验证码错误");
        }
        redisTemplate.delete(emailVCKey); // 删除redis中邮箱验证码


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

    /**
     * 退出登录
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 从请求头里后去token
        String token = request.getHeader(SecurityConstant.AUTHORIZATION_HEAD);
        //如果header中不存在Authorization，则从参数中获取Authorization
        if (!StringUtils.hasLength(token)){
            token = request.getParameter(SecurityConstant.AUTHORIZATION_HEAD);
        }

        System.out.println("token is "+token);

        // 校验token      判断是否为空、前缀是否正确
        if (!(StringUtils.hasLength(token) && token.startsWith(SecurityConstant.TOKEN_PREFIX))){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        // 截取token
        token = token.substring(7);

        // 检查token是否有效  并且获取通过token获取用户id
        String userId = null;
        try {
            userId = JwtUtils.getUserIdByJwtToken(token);
        }catch (Exception e){
            throw new BusinessException(EventCode.INVALID_TOKEN);
        }

        // 根据用户id清除redis缓存的用户信息
        try {
            redisTemplate.delete(UserConstant.USER_LOGIN_INFO+userId);
        }catch (Exception e){
            throw new BusinessException(EventCode.SYSTEM_ERROR);
        }

        return 1;
    }

    @Override
    public UserSpaceVo findUserSpace(HttpServletRequest request) {
        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        // 根据id 获取用户信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        User user = this.baseMapper.selectOne(queryWrapper);

        // 封装用户空间数据
        UserSpaceVo userSpaceVo = new UserSpaceVo();
        userSpaceVo.setUseSpace(user.getUseSpace());
        userSpaceVo.setTotalSpace(user.getTotalSpace());

        return userSpaceVo;
    }

    @Override
    public void spaceRefresh(String userId) {
        
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    public User getSafetyUser(User originUser){
        if (originUser == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setUserId(originUser.getUserId());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUseSpace(originUser.getUseSpace());
        safetyUser.setTotalSpace(originUser.getTotalSpace());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setAvatar(originUser.getAvatar());
        safetyUser.setNickName(originUser.getNickName());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUpdateTime(originUser.getUpdateTime());
        return safetyUser;
    }


}




