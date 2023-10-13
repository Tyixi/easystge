package com.yixi.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixi.file.model.entity.EFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author yixi
*/
@Mapper
public interface EFileMapper extends BaseMapper<EFile> {


    /**
     * 根据用户id查询用户使用空间
     * @param userId
     * @return
     */
    Long selectUserUseSpace(@Param("userId") String userId);

}




