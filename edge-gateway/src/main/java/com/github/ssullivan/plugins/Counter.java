package com.github.ssullivan.plugins;

import com.netflix.servo.DefaultMonitorRegistry;
import com.netflix.servo.monitor.BasicCounter;
import com.netflix.servo.monitor.MonitorConfig;
import com.netflix.servo.tag.InjectableTag;
import com.netflix.servo.tag.Tag;
import com.netflix.zuul.monitoring.CounterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by catal on 3/14/2017.
 */
public class Counter extends CounterFactory {
    private ConcurrentMap<String, BasicCounter> _counterMap = new ConcurrentHashMap<>();
    private Object lock = new Object();

    @Override
    public void increment(String name) {
        BasicCounter counter = getCounter(name);
        counter.increment();
    }

    private BasicCounter getCounter(String name) {
        BasicCounter counter = _counterMap.get(name);
        if (counter == null) {
            synchronized (lock) {
                counter = _counterMap.get(name);
                if (counter != null) {
                    return counter;
                }

                List<Tag> tags = new ArrayList<>(2);
                tags.add(InjectableTag.HOSTNAME);
                tags.add(InjectableTag.IP);
                counter = new BasicCounter(MonitorConfig.builder(name).withTags(tags).build());
                _counterMap.putIfAbsent(name, counter);
                DefaultMonitorRegistry.getInstance().register(counter);
            }
        }
        return counter;
    }
}
