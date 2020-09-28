package zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author : Kasa
 * @date : 2020/9/28 18:00
 * @descripthon :
 */
public class ZKSetData {
    private ZooKeeper zooKeeper = null;

    /**
     * 获取连接
     */
    @Before
    public void before() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper("dev201:2181", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功");
                    countDownLatch.countDown();
                }
            }
        });
        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
        System.out.println("SessionId = " + zooKeeper.getSessionId());
    }

    /**
     * 关闭连接
     */
    @After
    public void after() throws InterruptedException {
        if (zooKeeper != null) {
            System.out.println("关闭成功");
            zooKeeper.close();
        }
    }

    @Test
    public void setSync(){
        try {
            Stat stat = zooKeeper.setData(
                    "/kasa_test/create11",
                    "new value kkk 大订单".getBytes(),
                    -1);
            System.out.println("stat = " + stat);
        } catch (KeeperException | InterruptedException e) {
            assert false;
            e.printStackTrace();
        }
    }

    @Test
    public void setAsync(){
        zooKeeper.setData(
            "/kasa_test/create11",
            "new value setAsync 11大订单".getBytes(),
            4,
            new AsyncCallback.StatCallback() {
                @Override
                public void processResult(int i, String s, Object o, Stat stat) {
                    if (i<0){
                        System.out.println("异步更新节点信息-失败");
                        System.out.println("stat = " + stat);
                    }else {
                        System.out.println("异步更新节点信息-成功");
                        System.out.println("stat = " + stat);
                    }
                }
            },
            "setAsync"
        );
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

}
