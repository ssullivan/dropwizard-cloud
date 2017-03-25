package com.github.ssullivan.guice;

import com.github.ssullivan.eureka.Jersey2DefaultEurekaServerContext;
import com.github.ssullivan.eureka.PeerJersey2EurekaNodes;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.netflix.eureka.DefaultEurekaServerConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.aws.AwsBinderDelegate;
import com.netflix.eureka.registry.AbstractInstanceRegistry;
import com.netflix.eureka.registry.AwsInstanceRegistry;
import com.netflix.eureka.registry.InstanceRegistry;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.DefaultServerCodecs;
import com.netflix.eureka.resources.ServerCodecs;

/**
 * Created by catal on 3/24/2017.
 * This is sourced from <a
 * href="https://github.com/Netflix/eureka/blob/master/eureka-server-governator/src/main/java/com/netflix/eureka/guice/LocalDevEurekaServerModule.java">Ec2EurekaServerModule</a>
 */
public class Ec2EurekaServerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EurekaServerConfig.class).to(DefaultEurekaServerConfig.class).in(Scopes.SINGLETON);
        bind(PeerJersey2EurekaNodes.class).in(Scopes.SINGLETON);

        bind(AwsBinderDelegate.class).asEagerSingleton();

        // registry and interfaces
        bind(AwsInstanceRegistry.class).asEagerSingleton();
        bind(InstanceRegistry.class).to(AwsInstanceRegistry.class);
        bind(AbstractInstanceRegistry.class).to(AwsInstanceRegistry.class);
        bind(PeerAwareInstanceRegistry.class).to(AwsInstanceRegistry.class);

        bind(ServerCodecs.class).to(DefaultServerCodecs.class).in(Scopes.SINGLETON);

        bind(EurekaServerContext.class).to(Jersey2DefaultEurekaServerContext.class).in(Scopes.SINGLETON);
        bind(EurekaServerContextBootstrap.class).asEagerSingleton();
    }
}
