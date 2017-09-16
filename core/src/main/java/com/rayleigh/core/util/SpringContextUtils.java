package com.rayleigh.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;


@Service
public class SpringContextUtils implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(SpringContextUtils.class);
    //spring应用上下文
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        logger.info("Set Spring上下文");
        applicationContext = context;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    public static <T> T getBean(String name) throws BeansException {
        try {
            if (applicationContext == null || applicationContext.getBean(name) == null) {
                logger.error("获取bean: {} 失败", name);
                return null;
            }
            return (T) applicationContext.getBean(name);
        }catch (Exception e){
            logger.error("获取不到bean: {} 失败", name);
            return null;
        }
    }

    public static <T> T getBean(Class clazz) throws BeansException{
        try {
            if (applicationContext == null || applicationContext.getBean(clazz) == null) {
                logger.error("获取bean: {} 失败", clazz.getName());
                return null;
            }
            return (T) applicationContext.getBean(clazz);
        }catch (Exception e){
            logger.error("获取不到bean: {} 失败", clazz.getName());
            return null;
        }
    }
}