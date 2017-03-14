package com.github.ssullivan;

import com.github.ssullivan.lifecycle.ZuulGroovyManager;
import com.github.ssullivan.plugins.Counter;
import com.github.ssullivan.plugins.MetricPoller;
import com.github.ssullivan.plugins.ServoMonitor;
import com.github.ssullivan.plugins.Tracer;
import com.github.ssullivan.stats.monitoring.MonitorRegistry;
import com.netflix.servo.util.ThreadCpuStats;
import com.netflix.zuul.context.ContextLifecycleFilter;
import com.netflix.zuul.context.Debug;
import com.netflix.zuul.http.ZuulServlet;
import com.netflix.zuul.monitoring.CounterFactory;
import com.netflix.zuul.monitoring.MonitoringHelper;
import com.netflix.zuul.monitoring.TracerFactory;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Created by catal on 3/13/2017.
 */
public class ZuulWizardApplication extends Application<ZuulWizardConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZuulWizardApplication.class);
    private static final String ROOT_PREFIX = "/*";

    public static void main(String[] args) throws Exception {
        ZuulWizardApplication application = new ZuulWizardApplication();
        application.run(args);
    }

    @Override
    public String getName() {
        return "zuul-wizard";
    }

    @Override
    public void initialize(Bootstrap<ZuulWizardConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
    }

    private void initPlugins() {
        LOGGER.info("Registering Servo Monitor");
        MonitorRegistry.getInstance().setPublisher(new ServoMonitor());

        LOGGER.info("Starting Poller");
        MetricPoller.startPoller();


        LOGGER.info("Registering Servo Tracer");
        TracerFactory.initialize(new Tracer());

        LOGGER.info("Registering Servo Counter");
        CounterFactory.initialize(new Counter());

        LOGGER.info("Starting CPU stats");
        final ThreadCpuStats stats = ThreadCpuStats.getInstance();
        stats.start();
    }

    public void run(ZuulWizardConfiguration configuration, Environment environment) throws Exception {
        // Disable jersey because we don't need it for Zuul
        environment.jersey().disable();

        // FIXME: Possibly integrate with the dropwizard counters, investigate how Netflix actually uses this...
       // MonitoringHelper.initMocks();
        initPlugins();

        // Configure Servlets and Filters for Zuul
        LOGGER.info("Registering zuul handler with root path prefix: {}", ROOT_PREFIX);
        environment.servlets()
                .addServlet(ZuulServlet.class.getName(), ZuulServlet.class)
                .addMapping(ROOT_PREFIX);


        LOGGER.debug("Registering zuul container lifecycle filter with root path prefix: {}", ROOT_PREFIX);
        environment.servlets()
                .addFilter(ContextLifecycleFilter.class.getName(), ContextLifecycleFilter.class)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.ASYNC, DispatcherType.REQUEST, DispatcherType.ERROR,
                        DispatcherType.FORWARD, DispatcherType.INCLUDE), false,
                        ROOT_PREFIX);

        // TODO: Add Java based ZuulFilters

        environment.lifecycle()
                .manage(new ZuulGroovyManager());

        Debug.setDebugRequest(true);
        Debug.setDebugRouting(true);

    }
}
