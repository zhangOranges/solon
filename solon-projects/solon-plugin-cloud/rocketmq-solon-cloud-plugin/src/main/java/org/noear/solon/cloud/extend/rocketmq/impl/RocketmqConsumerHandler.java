package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.extend.rocketmq.RocketmqProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author noear
 * @since 1.3
 * @since 1.11
 */
public class RocketmqConsumerHandler implements MessageListenerConcurrently {
    static Logger log = LoggerFactory.getLogger(RocketmqConsumerHandler.class);

    final CloudEventObserverManger observerManger;
    final RocketmqConfig  config;

    public RocketmqConsumerHandler(RocketmqConfig config, CloudEventObserverManger observerManger) {
        this.observerManger = observerManger;
        this.config =config;
    }


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        boolean isOk = true;

        try {
            for (MessageExt message : list) {
                String topicNew = message.getTopic();
                String group = null;
                String topic = null;
                if(topicNew.contains(RocketmqProps.GROUP_SPLIT_MARK)){
                    group = topicNew.split(RocketmqProps.GROUP_SPLIT_MARK)[0];
                    topic = topicNew.split(RocketmqProps.GROUP_SPLIT_MARK)[1];
                }else{
                    topic = topicNew;
                }

                Event event = new Event(topic, new String(message.getBody()));
                event.tags(message.getTags());
                event.key(message.getKeys());
                event.times(message.getReconsumeTimes());
                event.channel(config.getChannelName());
                if(Utils.isNotEmpty(group)){
                    event.group(group);
                }

                isOk = isOk && onReceive(event, topicNew); //可以不吃异常
            }
        } catch (Throwable e) {
            isOk = false;
            EventBus.pushTry(e);
        }

        if (isOk) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } else {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

    /**
     * 处理接收事件
     */
    protected boolean onReceive(Event event, String topicNew) throws Throwable {
        boolean isOk = true;
        CloudEventHandler handler = null;

        if (Utils.isEmpty(event.tags())) {
            handler = observerManger.getByTopicAndTag(topicNew, "*");
        } else {
            handler = observerManger.getByTopicAndTag(topicNew, event.tags());

            if (handler == null) {
                handler = observerManger.getByTopicAndTag(topicNew, "*");
            }
        }

        if (handler != null) {
            isOk = handler.handle(event);
        }else{
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]", topicNew);
        }

        return isOk;
    }
}
