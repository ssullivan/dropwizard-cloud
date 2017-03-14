package com.github.ssullivan;

import com.github.ssullivan.lifecycle.ZuulGroovyManager;
import com.netflix.zuul.context.ContextLifecycleFilter;
import com.netflix.zuul.http.ZuulServlet;
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

    public void run(ZuulWizardConfiguration configuration, Environment environment) throws Exception {
        // Disable jersey because we don't need it for Zuul
        environment.jersey().disable();

        // Configure Servlets and Filters for Zuul
        LOGGER.info("Registering zuul handler with root path prefix: {}", ROOT_PREFIX);
        environment.servlets()
                .addServlet(ZuulServlet.class.getName(), ZuulServlet.class)
                .addMapping(ROOT_PREFIX);


        LOGGER.debug("Registering zuul container lifecycle filter with root path prefix: {}", ROOT_PREFIX);
        environment.servlets()
                .addFilter(ContextLifecycleFilter.class.getName(), ContextLifecycleFilter.class)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.ASYNC, DispatcherType.REQUEST), false,
                        ROOT_PREFIX);

        environment.lifecycle()
                .manage(new ZuulGroovyManager());

    }
}
