package com.yixi.ucenter;

import com.yixi.ucenter.mapper.UserLogMapper;
import com.yixi.ucenter.model.entity.UserLog;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author yixi
 * @date 2023/8/11
 * @apiNote
 */
@SpringBootTest
public class TestDemo {

    @Resource
    private UserLogMapper userLogMapper;

    @Test
    public void test(){
        UserLog userLog = new UserLog();
        userLog.setUserId("1688855665715101697");
        userLog.setLogEvent("用户登录");
        userLog.setLogDesc("邮箱密码登录");
        userLog.setLogTime(new Date());

        int insert = userLogMapper.insert(userLog);
        System.out.println("insert is "+insert);
    }
}
