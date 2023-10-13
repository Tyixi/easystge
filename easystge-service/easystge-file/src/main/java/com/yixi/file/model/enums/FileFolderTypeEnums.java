package com.yixi.file.model.enums;

/**
 * @author yixi
 * @date 2023/8/30
 * @apiNote
 */
public enum FileFolderTypeEnums {
    FILE(0, "文件"),
    FOLDER(1, "目录");

    private Integer type;
    private String desc;

    FileFolderTypeEnums(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
