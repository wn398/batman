package com.rayleigh.batman.util;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
/**
 * Created by wangn20 on 2017/7/4.文件压缩
 */
public class FileCompressUtil{
    private static Logger logger = LoggerFactory.getLogger(FileCompressUtil.class);

    /**
     *
     * @param sourceFileDir 源目录地址
     * @param targetFile  目的文件地址
     */
    public static void compress(File sourceFileDir,File targetFile) {
        if (!sourceFileDir.exists()){
            throw new RuntimeException(sourceFileDir + "不存在！无法压缩");
        }
        Project prj = new Project();
        Zip zip = new Zip();
        zip.setProject(prj);    
        zip.setDestFile(targetFile);
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);    
        fileSet.setDir(sourceFileDir);
        zip.addFileset(fileSet);    
        zip.execute();    
    }
}