package com.kevin.scepter.client.core.message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author: kevin
 * @description: 接收数据定时器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:52
 */
public class AutoReceiveEvent {

    CountDownLatch cdl;

    public AutoReceiveEvent() {
        //创建计数为1的CountDownLatch
        this.cdl = new CountDownLatch(1);
    }

    public AutoReceiveEvent(int waitCount) {
        this.cdl = new CountDownLatch(waitCount);
    }

    /**
     * countDown - 1
     */
    public void set() {
        this.cdl.countDown();
    }

    /**
     * 设置CountDownLatch await 超时等待时间
     *
     * @param seconds - 等待秒
     * @return boolean
     * @throws InterruptedException
     */
    public boolean waitOne(long seconds) throws InterruptedException {
        return this.cdl.await(seconds, TimeUnit.SECONDS);
    }

}
