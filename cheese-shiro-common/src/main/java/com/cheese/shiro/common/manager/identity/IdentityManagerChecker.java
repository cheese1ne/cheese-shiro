package com.cheese.shiro.common.manager.identity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 检查 IdentityManager 的 初始化情况
 * @author sobann
 */
public class IdentityManagerChecker {
    private static final Logger logger = LoggerFactory.getLogger(IdentityManagerChecker.class);
    private ScheduledExecutorService scheduler;
    private int threadNum = 1;
    private int interval = 10;


    public IdentityManagerChecker() {
    }

    public IdentityManagerChecker(int threadNum, int interval) {
        this.threadNum = threadNum;
        this.interval = interval;
    }

    /**
     * 初始化
     */
    public void init(){
        scheduler = Executors.newScheduledThreadPool(threadNum);
    }

    /**
     * 检查是否初始化完成
     * @return
     */
    public boolean identityManagerIsOk(){
        boolean initialized = IdentityManager.initialized();
        if(!initialized){
            logger.error("IdentityManager Is Not Initialized, It Can Not Take Effect !!! Ensure Client Will Register to Server, And Wait Config Sync");
        }
        return initialized;
    }

    /**
     * 关闭资源
     */
    public void close(){
        if(scheduler!=null){
            scheduler.shutdown();
        }
        logger.info("Close Scheduler Of IdentityManagerChecker");
    }

    /**
     * 开启检查任务
     */
    public void start(){
        init();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                //未初始化，再次进行检查
                if(!identityManagerIsOk()){
                    scheduler.schedule(this,interval,TimeUnit.SECONDS);
                }else {
                    //初始化完成，关闭资源
                    logger.info("IdentityManager Is Initialized, It Can Take Effect !!! Check Task Will be Destroy");
                    close();
                }
            }
        };
        scheduler.schedule(task,0,TimeUnit.SECONDS);
    }

}
