package com.rayleigh.batman.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

@Service
public class BuildServiceImpl implements BuildService{
    private static Logger logger = LoggerFactory.getLogger(BuildServiceImpl.class);
    //编译打包jar包
    @Async
    public void deployJarFile(File sourceDir) {
        Map<String, String> map = System.getenv();
        String os=System.getProperty("os.name");
        String m2= map.get("M2_HOME");
        ProcessBuilder processBuilder;
        if(os.startsWith("Windows")) {
            processBuilder = new ProcessBuilder(m2 + "\\bin\\mvn.cmd","clean", "deploy","-f","standard-pom.xml");
        }else{
            processBuilder = new ProcessBuilder(m2 + "/bin/mvn","clean","deploy","-f","standard-pom.xml");
        }
        processBuilder.directory(sourceDir);
        try {
            Process process = processBuilder.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                logger.info(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
