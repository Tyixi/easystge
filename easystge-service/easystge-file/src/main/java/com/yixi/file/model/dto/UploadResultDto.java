package com.yixi.file.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yixi
 * @date 2023/8/25
 * @apiNote
 */
@Data
public class UploadResultDto implements Serializable {

    private String fileId;
    private String status;
}
