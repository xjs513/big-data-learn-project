package com.evente;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author : Kasa
 * @date : 2020/9/29 16:27
 * @descripthon :
 */
public class ZKConnectionWatcher implements Watcher {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);


    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper = new ZooKeeper("dev201:21811", 5000,
                    new ZKConnectionWatcher());
            countDownLatch.await();
            // 打印会话ID
            System.out.println(zooKeeper.getSessionId());
            Thread.sleep(5000);
            zooKeeper.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("watchedEvent = " + watchedEvent);
        if (watchedEvent.getType() == Event.EventType.None){
            if (watchedEvent.getState()==  Event.KeeperState.SyncConnected){
                System.out.println("连接创建成功!");
                countDownLatch.countDown();
            } else if (watchedEvent.getState()==  Event.KeeperState.Disconnected){
                System.out.println("断开连接!");
            } else if (watchedEvent.getState()==  Event.KeeperState.Expired){
                System.out.println("会话超时!");
            } else if (watchedEvent.getState()==  Event.KeeperState.AuthFailed){
                System.out.println("认证失败!");
            }
        }
    }
}
