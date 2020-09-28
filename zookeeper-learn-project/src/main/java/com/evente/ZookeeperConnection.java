package com.evente;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author : Kasa
 * @date : 2020/9/28 14:57
 * @descripthon :
 */
public class ZookeeperConnection {
    public static void main(String[] args) {
        // 由于连接的创建是异步的, 因此创建一个计数器
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            ZooKeeper zooKeeper = new ZooKeeper("dev201:2181", 5000, new Watcher() {
                // 监视器对象
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected){
                        System.out.println("连接创建成功!");
                        countDownLatch.countDown();
                    }
                }
            });
            // 主线程阻塞等待连接对象创建完成
            countDownLatch.await();
            // 打印会话编号
            System.out.println(zooKeeper.getSessionId());

            zooKeeper.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
