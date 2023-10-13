package com.yixi.file.model.enums;

/**
 * @author yixi
 * @date 2023/9/7
 * @apiNote
 */
public enum FileDelFlagEnums {
    USING(1, "使用中"),
    RECYCLE (2, "回收站"),
    DEL(0, "删除");

    private Integer flag;
    private String desc;

    FileDelFlagEnums(Integer flag, String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    public Integer getFlag() {
        return flag;
    }

    public String getDesc() {
        return desc;
    }
}
