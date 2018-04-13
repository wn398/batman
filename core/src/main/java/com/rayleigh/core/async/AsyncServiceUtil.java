package com.rayleigh.core.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class AsyncServiceUtil<T> {
    Logger logger = LoggerFactory.getLogger(AsyncServiceUtil.class);

    @Async
    public Future<T> doAsync(Supplier<Future<T>> supplier) {
        return supplier.get();
    }

    @Async
    public void doAsync(T t,Consumer<T> consumer){
        consumer.accept(t);
    }

    private void demoTest() {

        this.doAsync(() -> {
            logger.info("任务一开始");
            try {
                Thread.sleep(9000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info("任务一完成");
            //return null;
            return new AsyncResult("任务一完成");
        });
    }

    public static void main(String[] args) {
        AsyncServiceUtil asyncServiceUtil = new AsyncServiceUtil();
        asyncServiceUtil.demoTest();
    }
}

