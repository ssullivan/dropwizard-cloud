package com.github.ssullivan.eureka;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.eureka.DefaultEurekaServerContext;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;

import javax.inject.Inject;

/**
 * Created by catal on 3/24/2017.
 */
public class Jersey2DefaultEurekaServerContext extends DefaultEurekaServerContext {
    @Inject
    /* package */ Jersey2DefaultEurekaServerContext(EurekaServerConfig serverConfig,
                                             ServerCodecs serverCodecs,
                                             PeerAwareInstanceRegistry registry,
                                             PeerJersey2EurekaNodes peerEurekaNodes,
                                             ApplicationInfoManager applicationInfoManager) {
        super(serverConfig, serverCodecs, registry, peerEurekaNodes, applicationInfoManager);
    }

}
