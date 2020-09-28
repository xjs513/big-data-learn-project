package zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author : Kasa
 * @date : 2020/9/24 11:07
 * @descripthon :
 */
public class ZKCreate {
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
        System.out.println("SessionId = "  +   zooKeeper.getSessionId());
    }

    /**
     * 同步创建节点
     */
    @Test
    public void create1() throws KeeperException, InterruptedException {
        /*
         * 第一个参数：节点的路径
         * 第二个参数：节点的数据
         * 第三个参数：权限列表 ZooDefs.Ids.OPEN_ACL_UNSAFE:world:anyone:cdrwa /
         * 第四个参数：节点的类型: 持久化节点
         */
        zooKeeper.create("/hello", "helloworld".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void create2() throws KeeperException, InterruptedException {
        /*
         * 第一个参数：节点的路径
         * 第二个参数：节点的数据
         * 第三个参数：权限列表 ZooDefs.Ids.READ_ACL_UNSAFE:world:anyone:r /
         * 第四个参数：节点的类型: 持久化节点
         */
        zooKeeper.create("/hello", "helloworld".getBytes(), ZooDefs.Ids.READ_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void create3() throws KeeperException, InterruptedException {
        /*
         * 第一个参数：节点的路径
         * 第二个参数：节点的数据
         * 第三个参数：权限列表
         *      world 授权模式
         * 第四个参数：节点的类型: 持久化节点
         */

        List<ACL> acls = new ArrayList<>();
        Id id = new Id("world",  "anyone");
        acls.add(new ACL(1, id));
        acls.add(new ACL(2, id));

        zooKeeper.create("/create3", "create3".getBytes(), acls, CreateMode.PERSISTENT);
    }

    @Test
    public void create4() throws KeeperException, InterruptedException {
        // ip 授权模式
        List<ACL> acls = new ArrayList<>();
        Id id = new Id("ip",  "192.168.1.129");
        acls.add(new ACL(31, id));
        zooKeeper.create("/create_ip", "create_ip".getBytes(), acls, CreateMode.PERSISTENT);
    }

    @Test
    public void create5() throws KeeperException, InterruptedException {
        // auth 授权模式
        // 添加授权用户
        zooKeeper.addAuthInfo("digest", "itcast:123456".getBytes());
        zooKeeper.create("/create_ip", "create_ip".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
    }

    @Test
    public void create6() throws KeeperException, InterruptedException {
        // auth 授权模式
        // 添加授权用户
        zooKeeper.addAuthInfo("digest", "itcast:123456".getBytes());
        // 权限列表
        List<ACL> acls = new ArrayList<>();
        Id id = new Id("auth",  "itcast");
        acls.add(new ACL(ZooDefs.Perms.READ, id));

        zooKeeper.create("/create_ip", "create_ip".getBytes(), acls, CreateMode.PERSISTENT);
    }

    @Test
    public void create7() throws KeeperException, InterruptedException {
        // digest 授权模式
        // 权限列表
        List<ACL> acls = new ArrayList<>();
        Id id = new Id("digest",  "itheima:qlzQzCLKhBROghkooLvb+Mlwv4A=");
        acls.add(new ACL(ZooDefs.Perms.ALL, id));
        String s = zooKeeper.create("/create7", "create7".getBytes(), acls, CreateMode.PERSISTENT);
    }

    @Test
    public void create8() throws KeeperException, InterruptedException {
        /*
         * 持久化 顺序节点
         */
        String result = zooKeeper.create("/kasa_test/create8", "create8".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL
        );
        System.out.println("result = " + result);
    }

    @Test
    public void create9() throws KeeperException, InterruptedException {
        /*
         * 创建临时节点 连接断开后自动删除
         */
        String result = zooKeeper.create("/kasa_test/create9", "create9".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL
        );
        System.out.println("result = " + result);
    }

    @Test
    public void create10() throws KeeperException, InterruptedException {
        /*
         * 创建临时有序节点 连接断开后自动删除
         */
        String result = zooKeeper.create("/kasa_test/create10", "create10".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL
        );
        System.out.println("result = " + result);
    }

    /**
     * 异步创建节点
     */
    @Test
    public void create11() throws KeeperException, InterruptedException {
        /*
         * 第一个参数：节点的路径
         * 第二个参数：节点的数据
         * 第三个参数：权限列表 ZooDefs.Ids.OPEN_ACL_UNSAFE:world:anyone:cdrwa /
         * 第四个参数：节点的类型: 持久化节点
         * 第五个参数：异步回调接口
         * 第六个参数：上下文参数
         */
        zooKeeper.create(
                "/kasa_test/create11",
                "create11".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                new AsyncCallback.StringCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx, String name) {
                        // 0 代表创建成功
                        System.out.println(rc);
                        // 节点的路径
                        System.out.println(path);
                        // 上下文的参数
                        System.out.println(ctx);
                        // 节点的路径
                        System.out.println(name);
                    }
                },
                "i am context");
        System.out.println("创建完成");
        TimeUnit.SECONDS.sleep(1);
    }






    /**
     * 关闭连接
     */
    @After
    public void after() throws InterruptedException {
        if (zooKeeper != null) {
//            TimeUnit.SECONDS.sleep(60);
            System.out.println("关闭成功");
            zooKeeper.close();
        }
    }
}
