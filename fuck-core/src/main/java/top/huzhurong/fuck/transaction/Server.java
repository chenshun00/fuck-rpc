package top.huzhurong.fuck.transaction;

/**
 * @author chenshun00@gmail.com
 * @since 2018/11/30
 */
public interface Server {

    void bind(Integer post);

    void unRegister() throws InterruptedException;
}
