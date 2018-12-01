package top.huzhurong.fuck.transaction;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/30
 */
public interface Server {

    void bind(Integer post);

    void unRegister() throws InterruptedException;
}
