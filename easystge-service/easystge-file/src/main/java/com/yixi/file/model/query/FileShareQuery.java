package com.yixi.file.model.query;

import com.baomidou.mybatisplus.annotation.TableId;
import com.yixi.common.utils.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yixi
 * @date 2023/9/24
 * @apiNote
 */
@Data
public class FileShareQuery extends PageRequest implements Serializable {

    /**
     * 主键
     */
    @TableId
    private String shareId;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件父id
     */
    private String filePid;

    /**
     * 文件名
     */
    private String fileName;

    private static final long serialVersionUID = -9131018546674251915L;
}
