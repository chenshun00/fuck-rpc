package top.huzhurong.fuck.register.zk;

import com.github.zkclient.ZkClient;
import org.junit.Before;
import org.junit.Test;
import top.huzhurong.fuck.register.IRegister;
import top.huzhurong.fuck.transaction.support.Provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public class ZkRegisterTest {

    private List<Provider> providers = new ArrayList<>(4);
    private IRegister register = new ZkRegister("127.0.0.1:2181");

    @Before
    public void before() {
        {
            Provider provider = new Provider();
            provider.setHost("127.0.0.1");
            provider.setPort(4376);
            provider.setServiceName("top.huzhurong.fuck.UserService");
            provider.setVersion("1.0.0");
            providers.add(provider);
        }
        {
            Provider provider = new Provider();
            provider.setHost("127.0.0.1");
            provider.setPort(2387);
            provider.setServiceName("top.huzhurong.fuck.UserService");
            provider.setVersion("1.0.0");
            providers.add(provider);
        }
        {
            Provider provider = new Provider();
            provider.setHost("127.0.0.1");
            provider.setPort(8336);
            provider.setServiceName("top.huzhurong.fuck.CheckService");
            provider.setVersion("2.0.0");
            providers.add(provider);
        }
    }

    @Test
    public void registerService() throws InterruptedException {
        register.subscribe("top.huzhurong.fuck.UserService");
        Thread.sleep(100000000000L);
    }

    @Test
    public void re() throws InterruptedException {
        Provider provider = new Provider();
        provider.setHost("227.2.22.100");
        provider.setPort(43276);
        provider.setServiceName("top.huzhurong.fuck.UserService");
        provider.setVersion("0.0.1");
        provider.setWeight(11);
        provider.setSerialization("jdk");
        Thread.sleep(5000);
        register.registerService(Collections.singletonList(provider));
        Thread.sleep(100000000000L);
    }

    @Test
    public void discover() throws InterruptedException {
        List<Provider> discover = register.discover("top.huzhurong.fuck.UserService", "1.0.0");
        discover.forEach(System.out::println);
        discover.forEach(provider -> {
            String path = ZkRegister.root_path + "/" + provider.getServiceName() + ZkRegister.root_provider;
            ((ZkRegister) register).getZkClient().subscribeChildChanges(path, (parent, children) -> {
                System.out.println(parent);
                children.forEach(System.out::println);
                System.out.println("=============");
            });
        });
        Thread.sleep(100000000000L);
    }

    @Test
    public void testCreatePath() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZkRegister zkRegister = (ZkRegister) this.register;
        ZkClient zkClient = zkRegister.getZkClient();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                try {
                    countDownLatch.await();
                    System.out.println(111);
                    doo(zkClient);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.setDaemon(false);
            thread.start();
        }
        System.out.println(countDownLatch.getCount());
//        Thread.sleep(2000L);
//        countDownLatch.countDown();
    }

    public void doo(ZkClient zkClient) {
        try {
            zkClient.createEphemeral("/item_pub_chen_lock_me");
            System.out.println("good");
        } catch (Exception ex) {
            System.out.println("GGG");
        }
    }
}
