package com.yixi.common.model.enums;

/**
 * 文件删除状态 0：正常  1：回收站
 *
 * @author yixi
 * @date 2023/8/15
 * @apiNote
 */
public enum FileDelEnum {
    RECYCLE(1,"回收站"),
    NORMAL(0,"正常");

    private Integer isDelete;
    private String desc;

    FileDelEnum(Integer isDelete, String desc) {
        this.isDelete = isDelete;
        this.desc = desc;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public String getDesc() {
        return desc;
    }
}
