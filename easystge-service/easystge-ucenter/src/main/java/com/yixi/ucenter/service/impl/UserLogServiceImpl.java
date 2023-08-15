package com.yixi.ucenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixi.ucenter.model.entity.UserLog;
import com.yixi.ucenter.service.UserLogService;
import com.yixi.ucenter.mapper.UserLogMapper;
import org.springframework.stereotype.Service;

/**
* @author 晨曦
* @description 针对表【user_log(用户日志)】的数据库操作Service实现
* @createDate 2023-08-11 12:13:25
*/
@Service
public class UserLogServiceImpl extends ServiceImpl<UserLogMapper, UserLog>
    implements UserLogService{

}




