package com.github.ssullivan.guice;

import com.github.ssullivan.eureka.MyDataCenterInstanceConfigProvider;
import com.google.common.collect.Lists;
import com.google.inject.*;
import com.google.inject.util.Modules;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.discovery.EurekaNamespace;
import com.netflix.discovery.guice.Jersey2EurekaModule;

import java.util.Collections;
import java.util.List;

/**
 * Created by catal on 3/24/2017.
 */
public final class EurekaInjectorCreator {
    // TODO: Probably don't need this anymore
    public static Injector createInjector() {
        return Guice.createInjector(
            modules()
        );
    }

    public static Module[] modules() {
        return new Module[] {Modules.override(
                new EurekaServerModule(),
                new Jersey2EurekaModule())
                .with(new AbstractModule() {
                    @Override
                    protected void configure() {
                        // the default impl of EurekaInstanceConfig is CloudInstanceConfig, which we only want in an AWS
                        // environment. Here we override that by binding MyDataCenterInstanceConfig to EurekaInstanceConfig.
                        bind(EurekaInstanceConfig.class).toProvider(MyDataCenterInstanceConfigProvider.class).in(Scopes.SINGLETON);
                    }
                }),
                new Ec2EurekaServerModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bindConstant().annotatedWith(EurekaNamespace.class).to("eureka");
                    }
                }};
    }
}
