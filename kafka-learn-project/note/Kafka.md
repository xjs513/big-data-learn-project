#  概念

##  开发相关

Producer、生产者 发送消息

Broker、集群中的服务代理节点

Consumer、消费者 消费消息

Zookeeper 集群 负责元数据的管理，控制器的选举等操作

##  Zookeeper 中存着 Kafka 的什么东西 ？？

Topic  主题，逻辑概念

Partition 分区

Offset 偏移量 每一个 Topic-Partition 对应一个 Offset

Offset 是消息在分区中的唯一标识，通过它来保证消息在分区内的有序性

Consumer Group



##  服务相关

Kafka 为分区引入了多副本(Replicas)机制来提升容灾能力。

同一分区中的不同副本保存的消息是相同的(同一时刻并不完全一样)，

副本之间是一主多从的关系，一个Leader + 多个Follower，

Leader 副本负责读写请求，Follower 副本只负责与Leader 副本同步消息。

一般情况下 Leader 副本中的消息相对 Leader 副本而言有一定的滞后。

------------------------------------------------------------

分区中所有的副本统称为 AR（As-singed Replicas）

所有与 Leader 副本保持一定程度同步的副本(包括 Leader) 组成 ISR (In-Sync Replicas)

与与 Leader 副本同步滞后过多的副本(不包括 Leader)组成 OSR (Out-of-Sync Replicas)

ISR OSR 都是 AR的子集，且有 AR = ISR + OSR

==同步滞后参数可以配置==

Follower 副本根据自身数据的同步状况在 ISR 和 OSR 之间来回移动。

Leader 副本故障时，只有 ISR 中的副本有机会通过选举成为 Leader 接管服务

OSR 中的副本则没有机会(这个原则也可以通过修改配置参数来改变)

------------------------------------------------------------

ISR 与 HW 和 LEO 也有紧密的关系。

HW 是 High Watermark 的缩写，即高水位线，它标识了一个特定 的offset, 

消费者只能拉取到这个offset之前的消息，不包括HW这条消息本身。

LEO和HW的关系如下：

==分区ISR集合中的每个副本都会维护自身的LEO，而ISR集合中最小的LEO即为分区的HW，消费者只能消费HW之前的消息==

```
Kafka 的复制机制既不是完全的同步复制，也不是单纯的异步复制，而是采用了ISR的方式很好的权衡了数据可靠性和性能之间的关系。
同步复制要求所有的follow都复制完成，消息才被确认成功提交，极大影响性能。
异步复制只要leader写入成功就认为成功提交了消息，如果此时leader发生故障而follower还没有复制完消息，那么数据会丢失。
```



LSO 是 Log Start Offset

LEO 是 Log End Offset 的缩写，标识当前日志文件中下一条待写入消息的 offset

、Log、Index、Segment、Replicas

watermark 的概念

Leader、Follow、Sync、Async



#  如何保证数据不丢失

##  Producer 端
acks = -1
数据丢失: 当 ISR 中只有 leader 时, 有丢失数据的可能
数据重复: 数据同步完成, ACK 未发送时, leader 挂掉了, 这时候数据会重复


##  Consumer 端

#  如何保证数据不重复消费

#  如何保证精准一次消费

