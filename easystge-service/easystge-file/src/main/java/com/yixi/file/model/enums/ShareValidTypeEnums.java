package com.yixi.file.model.enums;

/**
 * 设置分享code有效期
 * @author yixi
 * @date 2023/10/4
 * @apiNote
 */
public enum ShareValidTypeEnums {

    DAY_1(4, 1, "1天"),
    DAY_7(3, 7, "7天"),
    DAY_30(2, 30, "30天"),
    FOREVER(1, -1, "永久有效");

    /**
     * 存入数据库的参数
     */
    private Integer type;

    /**
     * 根据类型判断天数
     */
    private Integer days;

    /**
     * 枚举的参数描述
     */
    private String desc;

    ShareValidTypeEnums(Integer type, Integer days, String desc) {
        this.type = type;
        this.days = days;
        this.desc = desc;
    }


    public static ShareValidTypeEnums getByType(Integer type) {
        for (ShareValidTypeEnums typeEnums : ShareValidTypeEnums.values()) {
            if (typeEnums.getType().equals(type)) {
                return typeEnums;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public Integer getDays() {
        return days;
    }

    public String getDesc() {
        return desc;
    }



}
