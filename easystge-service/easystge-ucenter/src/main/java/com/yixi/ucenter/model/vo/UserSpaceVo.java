package com.yixi.ucenter.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yixi
 * @date 2023/9/5
 * @apiNote
 */
@Data
public class UserSpaceVo implements Serializable {
    /**
     * 使用空间单位byte
     */
    private Long useSpace;

    /**
     * 总空间
     */
    private Long totalSpace;


    private static final long serialVersionUID = 1L;

}
