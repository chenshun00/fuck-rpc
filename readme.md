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


#### Netty

*   每一个channel都有一个pipeline.   Pipeline <=====> channel
*   每一个channel都会有多个channelHandler，这些handler在channel初始化的时候设置上去
*   每一个handler都有一个context.    handler <====> context //一个handler就和一个绑定起来了
*   每一个pipeline都会将自己的channel的handler(上下文) channelHandlerContext串联成链表，head和tail由netty分配
*   每一个channel都会分配到一个NioEventLoop上,这个eventLopp可以netty传递进行的，也可以是netty自己的 (每一个NioEventloop都是单线程的)

> Note: `channel` 和 `NioEventLoop` 进行绑定..

[blog地址](https://www.yuque.com/chenshun00/ogqvw6)



