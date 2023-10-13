package com.yixi.file.client;

import com.yixi.common.user.UserInfo;
import com.yixi.common.utils.BaseResponse;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author yixi
 * @date 2023/8/22
 * @apiNote
 */
@Mapper
@FeignClient("service-ucenter")
public interface UcenterClient {

    /**
     * 远程调用ucenter服务
     * 根据用户id获取用户信息
     * @param id
     * @return
     */
    @GetMapping("/easystgeucenter/user/get/{id}")
    public UserInfo getUserInfoOrder(@PathVariable String id);


    /**
     * 远程调用ucenter服务
     * 更新用户信息
     * @param userInfo
     * @return
     */
    @PostMapping("/easystgeucenter/user/update")
    public BaseResponse updateUser(@RequestBody UserInfo userInfo);

}
