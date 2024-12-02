package com.dingCreator.astrology.event;


import com.dingCreator.astrology.event.event.BaseEvent;
import com.dingCreator.astrology.event.listener.EventListener;
import com.dingCreator.astrology.util.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author ding
 * @date 2024/10/5
 */
public class EventHelper {

    private static final Map<Type, HashSet<EventListener<? extends BaseEvent>>> EVENT_LISTENER_MAP = new HashMap<>();

    static Logger logger = LoggerFactory.getLogger(EventHelper.class);

    public static void addListener(EventListener<? extends BaseEvent> eventListener) {
        Type type = eventListener.getClass().getGenericSuperclass();
        HashSet<EventListener<?>> listeners = EVENT_LISTENER_MAP.getOrDefault(type, new HashSet<>());
        listeners.add(eventListener);
        EVENT_LISTENER_MAP.put(type, listeners);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseEvent> void syncSendEvent(T event) {
        Type type = event.getClass();
        EVENT_LISTENER_MAP.getOrDefault(type, new HashSet<>()).forEach(listener -> {
            try {
                ((EventListener<T>) listener).listen(event);
            } catch (ClassCastException e) {
                logger.error("event listened failed, event body:[{}], event listener:[{}]", event, listener, e);
            }
        });
    }

    public static <T extends BaseEvent> void asyncSendEvent(T event) {
        ThreadPoolUtil.execute(() -> syncSendEvent(event));
    }
}
