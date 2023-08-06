package com.yixi.sms.service;

import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.ResponseData;

import java.util.Map;

/**
 * @author yixi
 * @date 2023/8/5
 * @apiNote
 */
public interface SmsService {
    boolean sendMailVc(String code, String email) throws BusinessException;

}
