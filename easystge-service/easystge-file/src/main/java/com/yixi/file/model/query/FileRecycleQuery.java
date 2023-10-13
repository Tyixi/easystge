package com.yixi.file.model.query;

import com.baomidou.mybatisplus.annotation.TableId;
import com.yixi.common.utils.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author yixi
 * @date 2023/9/10
 * @apiNote
 */
@Data
public class FileRecycleQuery extends PageRequest implements Serializable {

    /**
     * 主键
     */
    @TableId
    private String recycleId;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 结束日期
     */
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = -9131018546674251945L;
}
