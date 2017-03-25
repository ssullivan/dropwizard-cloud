package com.github.ssullivan;

import com.github.ssullivan.guice.EurekaInjectorCreator;
import com.github.ssullivan.guice.EurekaServerModule;
import com.github.ssullivan.guice.EurekaServletModule;
import com.google.inject.*;
import com.netflix.discovery.EurekaNamespace;
import com.netflix.discovery.provider.DiscoveryJerseyProvider;
import com.netflix.eureka.EurekaServerContext;
import io.dropwizard.Application;
import io.dropwizard.Bundle;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.dropwizard.views.ViewConfigurable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.injector.InjectorFactory;

/**
 * Created by catal on 3/24/2017.
 */
public class EurekaServerApplication extends Application<EurekaServerConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaServerApplication.class);
    private GuiceBundle<EurekaServerConfiguration> _guiceBundle;

    public static void main(String[] args) throws Exception {

        EurekaServerApplication application = new EurekaServerApplication();
        EurekaServerBootstrap.initEurekaEnvironment();

        application.run(args);
    }

    @Override
    public String getName() {
        return "eureka-server";
    }

    @Override
    public void initialize(Bootstrap<EurekaServerConfiguration> bootstrap) {
        Injector parentInjector = Guice.createInjector(EurekaInjectorCreator.modules());

        _guiceBundle = GuiceBundle.<EurekaServerConfiguration>builder()
                .enableAutoConfig("com.github.ssullivan.rest", "com.netflix.eureka.resources")
                .modules(new EurekaServletModule())
                .injectorFactory((stage, modules) -> parentInjector.createChildInjector(modules))
                .build();
        bootstrap.addBundle(_guiceBundle);

        bootstrap.addBundle(new ViewBundle<>());
        bootstrap.addBundle(new Bundle() {
            @Override
            public void initialize(Bootstrap<?> bootstrap) {

            }

            @Override
            public void run(Environment environment) {
                environment.jersey()
                        .register(DiscoveryJerseyProvider.class);
            }
        });

        // TODO: Re-work gradle wro4j stuff
        bootstrap.addBundle(new AssetsBundle("/static/eureka", "/static/eureka"));
    }

    @Override
    public void run(EurekaServerConfiguration configuration, Environment environment) throws Exception {
    }
}

