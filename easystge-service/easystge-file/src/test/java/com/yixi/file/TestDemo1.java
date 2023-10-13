package com.yixi.file;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yixi.file.mapper.EFileMapper;
import com.yixi.file.model.dto.FolderTreeDto;
import com.yixi.file.model.entity.EFile;
import com.yixi.file.model.vo.FileRecycleVo;
import com.yixi.file.service.EFileService;
import com.yixi.file.service.FileRecycleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yixi
 * @date 2023/9/1
 * @apiNote
 */
@SpringBootTest
public class TestDemo1 {

    @Resource
    public EFileMapper eFileMapper;
    @Resource
    public EFileService eFileService;
    @Resource
    public FileRecycleService fileRecycleService;

    @Test
    public void insert(){
        EFile eFile = new EFile();
        eFile.setFileId("1697545390273503234");
        eFile.setFileName("haaha");
        int insert = eFileMapper.insert(eFile);
        System.out.println("insert is "+insert);
        System.out.println("eFile is " + eFile);
    }

    @Test
    public void folderTree(){
        List<FolderTreeDto> folderTree = eFileService.getFolderTree(null);
        System.out.println(folderTree.size());
        System.out.println(folderTree.get(2));

//        Iterator<FolderTreeDto> iterator = folderTree.iterator();
//        while (iterator.hasNext()){
//            FolderTreeDto item = iterator.next();
//            System.out.println(item);
//        }
    }

    @Test
    public void copyTest(){
        EFile eFile = new EFile();
        eFile.setFileId("12300000");
        eFile.setFilePid("123321");
        eFile.setFileName("name");
        System.out.println("eflie is "+eFile);
        FolderTreeDto folderTreeDto = new FolderTreeDto();
        BeanUtil.copyProperties(eFile, folderTreeDto, false);
        System.out.println("folderTree  is "+ folderTreeDto);
    }



}
