package com.github.ssullivan.model;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Pair;
import com.netflix.eureka.util.StatusInfo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper for the Netflix Eureka StatusInfo for use with mustache templates.
 */
public class EurekaStatusInfo {
    private StatusInfo _statusInfo;

    public EurekaStatusInfo(StatusInfo statusInfo) {
        _statusInfo = statusInfo;
    }

    public StatusInfo getStatusInfo() {
        return _statusInfo;
    }

    public Boolean isHealth() {
        return _statusInfo.isHealthy();
    }

    public InstanceInfo getInstanceInfo() {
        return _statusInfo.getInstanceInfo();
    }

    public List<Pair<String, String>> getGenerateStats() {
       return Collections.unmodifiableList(_statusInfo.getGeneralStats()
                .entrySet()
                .stream()
                .map((entrySet) -> new Pair<>(entrySet.getKey(), entrySet.getValue()))
                .collect(Collectors.toList()));
    }

    public List<Pair<String, String>> getApplicationStats() {
        return Collections.unmodifiableList(_statusInfo.getGeneralStats()
                .entrySet()
                .stream()
                .map((entrySet) -> new Pair<>(entrySet.getKey(), entrySet.getValue()))
                .collect(Collectors.toList()));
    }
}
