### RPC 的简单实现

基于 `netty4` 实现的一个简单的 `RPC` , 代码精简. just for learn.

#### 如何运行

*   安装 `lombok`
*   安装 `zookeeper`
*   相继运行 `fuck-test` 项目下的 `ProviderDemo` `ConsumerDemo` 即可查看实现结果

#### 同步和异步

tcp本身的双向通信是异步的,netty支持的nio底层也是异步。

* 同步:线程阻塞, wait(timeout),等待provider相应返回，解除线程的阻塞,返回给biz线程.
* 异步:线程不阻塞，支持返回，利用java8的 `CompletableFuture` 实现，可参见测试代码.