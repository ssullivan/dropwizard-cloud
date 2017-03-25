package com.github.ssullivan.eureka;

import com.google.inject.Inject;
import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaNamespace;

import javax.inject.Provider;

/**
 * Created by catal on 3/24/2017.
 */
public class MyDataCenterInstanceConfigProvider implements Provider<EurekaInstanceConfig> {
    @Inject(optional = true)
    @EurekaNamespace
    private String namespace;

    private MyDataCenterInstanceConfig config;

    @Override
    public synchronized MyDataCenterInstanceConfig get() {
        if (config == null) {
            if (namespace == null) {
                config = new MyDataCenterInstanceConfig();
            } else {
                // This is to fix a bug in AwsAsgUtil.getAccountId( ) where it asssumes
                // that the DataCenter is an instance of AmazonInfo
                config = new MyDataCenterInstanceConfig(namespace, AmazonInfo.Builder.newBuilder().autoBuild(namespace));
            }

            // TODO: Remove this when DiscoveryManager is finally no longer used
            DiscoveryManager.getInstance().setEurekaInstanceConfig(config);
        }
        return config;
    }
}
