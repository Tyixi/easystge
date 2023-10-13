package com.yixi.file.model.query;

import com.yixi.common.utils.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yixi
 * @date 2023/8/15
 * @apiNote
 */
@Data
public class FileQuery extends PageRequest implements Serializable {

    /**
     * 所属用户id
     */
    private String userId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 父级id
     */
    private String filePid;

    /**
     * 1:视频 2:音频 3:图片 4文档 5其他  文件分类
     */
    private Integer fileCategory;

    /**
     * 1: 正常 2: 回收站 3: 逻辑删除
     */
    private Integer delFlag;

    /**
     * 1:回收站  0:正常
     */
    private Integer isDeleted;


    private static final long serialVersionUID = -9131018546674251945L;

}
