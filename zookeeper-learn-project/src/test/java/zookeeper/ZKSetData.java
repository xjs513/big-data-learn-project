package zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

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
        zooKeeper = new ZooKeeper("kasa:2181", 5000, new Watcher() {
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
    public void setAsync() {
        zooKeeper.setData(
                "/kasa_test/create11",
                "new value setAsync 11大订单".getBytes(),
                4,
                new AsyncCallback.StatCallback() {
                    @Override
                    public void processResult(int i, String s, Object o, Stat stat) {
                        if (i < 0) {
                            System.out.println("异步更新节点信息-失败");
                            System.out.println("stat = " + stat);
                        } else {
                            System.out.println("异步更新节点信息-成功");
                            System.out.println("stat = " + stat);
                        }
                    }
                },
                "setAsync"
        );
    }


    @Test
    public void deleteSync(){
        try {
            zooKeeper.delete(
                    "/kasa_test/create11",
                    -1);
        } catch (KeeperException | InterruptedException e) {
            assert false;
            e.printStackTrace();
        }
    }

    @Test
    public void deleteAsync(){
        zooKeeper.delete(
                "/kasa_test/create11",
                -1,
                new AsyncCallback.VoidCallback() {
                    @Override
                    public void processResult(int i, String path, Object ctx) {
                        // 0 代表操作成功
                        System.out.println(i);
                        // 节点的路径
                        System.out.println(path);
                        // 上下文的参数
                        System.out.println(ctx);
                    }
                },
                "deleteAsync"
        );
    }

    @Test
    public void getSync(){
        try {
            Stat stat = new Stat();
            byte[] data = zooKeeper.getData(
                    "/kasa_test",
                    false, stat);
            System.out.println("new String(data) = " + new String(data));
            System.out.println("stat = " + stat.getAversion());
        } catch (KeeperException | InterruptedException e) {
            assert false;
            e.printStackTrace();
        }
    }

    @Test
    public void getAsync(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper.getData(
                "/kasa_test",
                false,
                new AsyncCallback.DataCallback() {
<<<<<<< HEAD
                    @Override
                    public void processResult(int i, String path, Object ctx, byte[] bytes, Stat stat) {
                        System.out.println("i = " + i);
                        System.out.println("path = " + path);
                        System.out.println("ctx = " + ctx);
                        System.out.println("bytes = " + new String(bytes));
                        System.out.println("stat = " + stat);
                        countDownLatch.countDown();
                    }
                },
                "getAsync"
        );
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getChildrenSync(){
        try {
            Stat stat = new Stat();
            List<String> children = zooKeeper.getChildren(
                    "/kasa_test",
                    false);
            for (String child : children) {
                System.out.println("child = " + child);
            }
        } catch (KeeperException | InterruptedException e) {
            assert false;
            e.printStackTrace();
        }
    }

    @Test
    public void getChildrenAsync(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper.getChildren(
                "/kasa_test",
                false,
                new AsyncCallback.ChildrenCallback() {
                    @Override
                    public void processResult(int i, String path, Object ctx, List<String> list) {
                        System.out.println("i = " + i);
                        System.out.println("path = " + path);
                        System.out.println("ctx = " + ctx);
                        for (String child : list) {
                            System.out.println("child = " + child);
                        }
                        countDownLatch.countDown();
                    }
                },
                "getChildrenAsync"
        );
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void existsSync(){
        try {
            Stat exists = zooKeeper.exists("/kasa_test_", false);
            System.out.println("exists = " + exists);
=======
                    @Override
                    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                        System.out.println("i = " + i);
                        System.out.println("new Strint(bt) = " + new String(bytes));
                    }
                },
                "getAsync"
        );
    }

    @Test
    public void getChildrenSync(){
        try {
            Stat stat = new Stat();
            List<String> children = zooKeeper.getChildren(
                    "/kasa_test",
                    false);
            children.forEach(child -> {
                System.out.println("child = " + child);
            });
>>>>>>> 09c976b8b8d54f165e2c80338b1e0268cf5436bb
        } catch (KeeperException | InterruptedException e) {
            assert false;
            e.printStackTrace();
        }
    }

    @Test
<<<<<<< HEAD
    public void existsAsync(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper.exists(
                "/kasa_test4",
                false,
                new AsyncCallback.StatCallback() {
                    @Override
                    public void processResult(int i, String path, Object ctx, Stat stat) {
                        System.out.println("i = " + i);
                        System.out.println("path = " + path);
                        System.out.println("ctx = " + ctx);
                        System.out.println("stat = " + stat);
                        countDownLatch.countDown();
=======
    public void getChildrenAsync(){
        zooKeeper.getChildren(
                "/kasa_test",
                false,
                new AsyncCallback.Children2Callback() {
                    @Override
                    public void processResult(int i, String s, Object o, List<String> list, Stat stat) {
                        System.out.println("i = " + i);
>>>>>>> 09c976b8b8d54f165e2c80338b1e0268cf5436bb
                    }
                },
                "existsAsync"
        );
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
