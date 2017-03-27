package com.github.ssullivan;

import com.netflix.config.ConfigurationManager;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.util.EurekaMonitors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by catal on 3/24/2017.
 */
/* package */ class EurekaServerBootstrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaServerBootstrap.class);

    private static final String ROOT_PREFIX = "/*";

    private static final String TEST = "test";

    private static final String ARCHAIUS_DEPLOYMENT_ENVIRONMENT = "archaius.deployment.environment";

    private static final String EUREKA_ENVIRONMENT = "eureka.environment";

    private static final String DEFAULT = "default";

    private static final String ARCHAIUS_DEPLOYMENT_DATACENTER = "archaius.deployment.datacenter";

    private static final String EUREKA_DATACENTER = "eureka.datacenter";

    private EurekaServerContext _eurekaServerContext;

    /* package */ static void initEurekaEnvironment() throws Exception {
        LOGGER.info("Setting the eureka configuration..");

        String dataCenter = ConfigurationManager.getConfigInstance()
                .getString(EUREKA_DATACENTER);
        if (dataCenter == null) {
            LOGGER.info(
                    "Eureka data center value eureka.datacenter is not set, defaulting to default");
            ConfigurationManager.getConfigInstance()
                    .setProperty(ARCHAIUS_DEPLOYMENT_DATACENTER, DEFAULT);
        }
        else {
            ConfigurationManager.getConfigInstance()
                    .setProperty(ARCHAIUS_DEPLOYMENT_DATACENTER, dataCenter);
        }
        String environment = ConfigurationManager.getConfigInstance()
                .getString(EUREKA_ENVIRONMENT);
        if (environment == null) {
            ConfigurationManager.getConfigInstance()
                    .setProperty(ARCHAIUS_DEPLOYMENT_ENVIRONMENT, TEST);
            LOGGER.info(
                    "Eureka environment value eureka.environment is not set, defaulting to test");
        }
        else {
            ConfigurationManager.getConfigInstance()
                    .setProperty(ARCHAIUS_DEPLOYMENT_ENVIRONMENT, environment);
        }
    }
}
