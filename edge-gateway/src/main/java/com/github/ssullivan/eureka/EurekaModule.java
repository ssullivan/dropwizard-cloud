package com.github.ssullivan.eureka;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.providers.MyDataCenterInstanceConfigProvider;

/**
 * This module is used to override the default Eureka Jersey2EurekaModule.
 *
 * Created by catal on 3/15/2017.
 */
public class EurekaModule extends AbstractModule {
    @Override
    protected void configure() {
        // the default impl of EurekaInstanceConfig is CloudInstanceConfig, which we only want in an AWS
        // environment. Here we override that by binding MyDataCenterInstanceConfig to EurekaInstanceConfig.
        bind(EurekaInstanceConfig.class).toProvider(MyDataCenterInstanceConfigProvider.class).in(Scopes.SINGLETON);
    }
}
