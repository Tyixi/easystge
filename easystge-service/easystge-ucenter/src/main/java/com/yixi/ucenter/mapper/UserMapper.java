package com.yixi.ucenter.mapper;

import com.yixi.ucenter.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yixi
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-08-05 22:05:19
* @Entity com.yixi.easystge.model.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




