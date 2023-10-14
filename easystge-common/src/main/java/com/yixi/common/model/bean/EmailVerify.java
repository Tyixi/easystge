package com.yixi.common.model.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yixi
 */
@Data
@AllArgsConstructor
public class EmailVerify implements Serializable {
    private String email;
    private String code;
}
