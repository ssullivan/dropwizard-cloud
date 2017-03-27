package com.github.ssullivan.guice;

import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.util.EurekaMonitors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by catal on 3/27/2017.
 */
public class EurekaServerContextBootstrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaServerContextBootstrap.class);
    private EurekaServerContext _eurekaServerContext;

    @Inject
    public EurekaServerContextBootstrap(EurekaServerContext eurekaServerContext) throws Exception {
        _eurekaServerContext = eurekaServerContext;
        initialize();
    }

    private void initialize() throws Exception {
        _eurekaServerContext.initialize();


        _eurekaServerContext.getRegistry()
                .initializedResponseCache();

        int registryCount = 0;
        registryCount = _eurekaServerContext.getRegistry().syncUp();

        _eurekaServerContext.getRegistry().openForTraffic(_eurekaServerContext.getApplicationInfoManager(), registryCount);

        EurekaMonitors.registerAllStats();
    }
}
