package com.github.ssullivan.plugins;

import com.netflix.servo.monitor.DynamicTimer;
import com.netflix.servo.monitor.MonitorConfig;
import com.netflix.servo.monitor.Stopwatch;
import com.netflix.servo.tag.InjectableTag;
import com.netflix.servo.tag.Tag;
import com.netflix.zuul.monitoring.TracerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by catal on 3/14/2017.
 */
public class Tracer extends TracerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(Tracer.class);
    static List<Tag> tags = new ArrayList<Tag>(2);

    static {
        tags.add(InjectableTag.HOSTNAME);
        tags.add(InjectableTag.IP);
    }

    @Override
    public com.netflix.zuul.monitoring.Tracer startMicroTracer(String name) {
        return new ServoTracer(name);
    }

    class ServoTracer implements com.netflix.zuul.monitoring.Tracer {
        final MonitorConfig config;
        final Stopwatch stopwatch;

        private ServoTracer(String name) {
            config = MonitorConfig.builder(name).withTags(tags).build();
            stopwatch = DynamicTimer.start(config, TimeUnit.MICROSECONDS);
        }

        @Override
        public void stopAndLog() {
            DynamicTimer.record(config, stopwatch.getDuration());
            LOGGER.debug("name: {}, tags: {}, duration: {}ms", config.getName(), config.getTags().asMap(), stopwatch.getDuration(TimeUnit.MILLISECONDS));
        }

        @Override
        public void setName(String name) {

        }
    }
}
