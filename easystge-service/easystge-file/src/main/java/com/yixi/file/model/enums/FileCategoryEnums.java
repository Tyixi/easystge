package com.yixi.file.model.enums;

/**
 * @author yixi
 * @date 2023/8/30
 * @apiNote
 */
public enum FileCategoryEnums {
    VIDEO(1,"视频"),
    AUDIO(2,"音频"),
    IMAGE(3,"图片"),
    DOC(4,"文档"),
    OTHER(5,"其它");


    private Integer category;
    private String desc;

    FileCategoryEnums(Integer category, String desc){
        this.category = category;
        this.desc = desc;
    }


    public Integer getCategory() {
        return category;
    }
}
