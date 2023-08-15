package com.yixi.ucenter.mapper;

import com.yixi.ucenter.model.entity.UserLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 晨曦
* @description 针对表【user_log(用户日志)】的数据库操作Mapper
* @createDate 2023-08-11 12:13:25
* @Entity com.yixi.ucenter.model.entity.UserLog
*/
@Mapper
public interface UserLogMapper extends BaseMapper<UserLog> {

}




