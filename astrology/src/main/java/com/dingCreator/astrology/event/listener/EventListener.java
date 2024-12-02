package com.dingCreator.astrology.event.listener;

import com.dingCreator.astrology.event.event.BaseEvent;

/**
 * @author ding
 * @date 2024/10/5
 */
public interface EventListener<T extends BaseEvent> {

    /**
     * 执行操作
     *
     * @param event 事件
     */
    void listen(T event);
}
