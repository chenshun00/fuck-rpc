package top.huzhurong.fuck.transaction;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public interface Client {

    void connect(String host, Integer port);

    void disConnect() throws InterruptedException;
}
