package com.yixi.file.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author yixi
 * @date 2023/9/2
 * @apiNote
 */
@Data
public class FolderTreeDto {
    private String fileId;
    private String fileName;
    private String filePid;
    private List<FolderTreeDto> childList;

    @Override
    public String toString() {
        return "FolderTreeDto{" +
                "fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filePid='" + filePid + '\'' +
                ", childList=" + childList +
                '}';
    }
}
