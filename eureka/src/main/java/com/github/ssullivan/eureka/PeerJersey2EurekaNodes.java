package com.github.ssullivan.eureka;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.cluster.Jersey2PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;

import javax.inject.Inject;

/**
 * Created by catal on 3/24/2017.
 */
public class PeerJersey2EurekaNodes extends Jersey2PeerEurekaNodes {
    @Inject
    public PeerJersey2EurekaNodes(PeerAwareInstanceRegistry registry,
                                  EurekaServerConfig serverConfig,
                                  EurekaClientConfig clientConfig,
                                  ServerCodecs serverCodecs,
                                  ApplicationInfoManager applicationInfoManager) {
        super(registry, serverConfig, clientConfig, serverCodecs, applicationInfoManager);
    }
}
