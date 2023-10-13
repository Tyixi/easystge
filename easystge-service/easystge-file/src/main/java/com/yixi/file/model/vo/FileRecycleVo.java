package com.yixi.file.model.vo;

import com.yixi.file.model.entity.FileRecycle;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yixi
 * @date 2023/9/10
 * @apiNote
 */
@Data
public class FileRecycleVo extends FileRecycle implements Serializable {

    /**
     * 0:文件 1:目录
     */
    private Integer folderType;

    /**
     * 1:视频 2:音频 3:图片 4文档 5其他  文件分类
     */
    private Integer fileCategory;

    /**
     * 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:zip 9:其他
     */
    private Integer fileType;

    /**
     * 有效时间
     */
    private long validTime;
}
