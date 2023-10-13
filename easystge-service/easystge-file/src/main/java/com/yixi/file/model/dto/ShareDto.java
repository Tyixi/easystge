package com.yixi.file.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yixi
 * @date 2023/10/5
 * @apiNote
 */
@Data
public class ShareDto {
    private String shareId;
    private String userId;
    private String fileId;
    private LocalDateTime endTime;



}
