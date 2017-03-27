package com.github.ssullivan.rest;

import com.github.ssullivan.EurekaServerConfiguration;
import com.github.ssullivan.model.EurekaStatusInfo;
import com.github.ssullivan.view.EurekaLastnView;
import com.github.ssullivan.view.EurekaStatusView;
import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Pair;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl;
import com.netflix.eureka.resources.StatusResource;
import com.netflix.eureka.util.StatusInfo;
import com.netflix.eureka.util.StatusUtil;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by catal on 3/26/2017.
 */
@Path("/dashboard")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
public class EurekaDashboardResource {
    private ApplicationInfoManager _applicationInfoManager;
    private EurekaServerContext _eurekaServerContext;
    private final StatusUtil _statusUtil;



    @Inject
    /* package */ EurekaDashboardResource(ApplicationInfoManager applicationInfoManager,
                                          EurekaServerContext eurekaServerContext,
                                          EurekaServerConfiguration eurekaServerConfiguration) {
        _applicationInfoManager = applicationInfoManager;
        _eurekaServerContext = eurekaServerContext;
        _statusUtil = new StatusUtil(_eurekaServerContext);
    }

    @GET
    public EurekaStatusView status(@Context HttpServletRequest httpServletRequest) {
        Map<String, Object> context = new HashMap<>();
        initViewContext(httpServletRequest, context);
        initApps(context);

        StatusInfo statusInfo;
        try {
            statusInfo = _statusUtil.getStatusInfo();
        } catch (Exception ignored) {
            statusInfo = StatusInfo.Builder.newBuilder()
                    .withInstanceInfo(_eurekaServerContext.getApplicationInfoManager().getInfo())
                    .isHealthy(false).build();
        }

        context.put("statusInfo", new EurekaStatusInfo(statusInfo));
        initInstanceInfo(context, statusInfo);
        filterReplicas(context, statusInfo);

        return new EurekaStatusView(context);
    }

    @GET
    @Path("/lastn")
    public EurekaLastnView lastn(@Context HttpServletRequest httpServletRequest) {
        Map<String, Object> context = new HashMap<>();
        initViewContext(httpServletRequest, context);
        PeerAwareInstanceRegistryImpl registry = (PeerAwareInstanceRegistryImpl) getRegistry();
        ArrayList<Map<String, Object>> lastNCanceled = new ArrayList<>();
        List<Pair<Long, String>> list = registry.getLastNCanceledInstances();
        for (Pair<Long, String> entry : list) {
            lastNCanceled.add(registeredInstance(entry.second(), entry.first()));
        }
        context.put("lastNCanceled", lastNCanceled);
        context.put("lastNCanceled_empty", lastNCanceled.isEmpty());

        list = registry.getLastNRegisteredInstances();
        ArrayList<Map<String, Object>> lastNRegistered = new ArrayList<>();
        for (Pair<Long, String> entry : list) {
            lastNRegistered.add(registeredInstance(entry.second(), entry.first()));
        }
        context.put("lastNRegistered", lastNRegistered);
        context.put("lastNRegistered_empty", lastNRegistered.isEmpty());
        return new EurekaLastnView(context);
    }

    private PeerAwareInstanceRegistry getRegistry() {
        return _eurekaServerContext.getRegistry();
    }

    private Map<String, Object> registeredInstance(String id, long timestampMillis) {
        Map<String, Object> retval = new HashMap<>();
        retval.put("id", id);
        retval.put("date", new Date(timestampMillis));
        return retval;
    }

    private Map<String, Object> initViewContext(HttpServletRequest httpServletRequest, Map<String, Object> context) {
        String contextPath = httpServletRequest.getContextPath();

        context.put("time", new Date());
        context.put("basePath", contextPath);
        context.put("dashboardPath", contextPath != null && contextPath.endsWith("/") ?
                contextPath + "dashboard" : contextPath + "/dashboard");

        initHeaderContext(context);
        initNavbarContext(context);

        return context;
    }

    private Map<String, Object> initHeaderContext(Map<String, Object> context) {
        context.put("currentTime", StatusResource.getCurrentTimeAsString());
        context.put("upTime", StatusInfo.getUpTime());
        context.put("environment", ConfigurationManager.getDeploymentContext().getDeploymentEnvironment());
        context.put("datacenter", ConfigurationManager.getDeploymentContext().getDeploymentDatacenter());

        PeerAwareInstanceRegistry registry = _eurekaServerContext.getRegistry();
        context.put("registry", registry);
        context.put("isBelowRenewThreshold", registry.isBelowRenewThresold() == 1);
        DataCenterInfo info = _applicationInfoManager.getInfo().getDataCenterInfo();
        if (info.getName() == DataCenterInfo.Name.Amazon) {
            AmazonInfo amazonInfo = (AmazonInfo) info;
            context.put("amazonInfo", amazonInfo);
            context.put("amiId", amazonInfo.get(AmazonInfo.MetaDataKey.amiId));
            context.put("availabilityZone",
                    amazonInfo.get(AmazonInfo.MetaDataKey.availabilityZone));
            context.put("instanceId", amazonInfo.get(AmazonInfo.MetaDataKey.instanceId));
        }
        return context;
    }

    private Map<String, Object> initNavbarContext(Map<String, Object> context) {
        Map<String, String> replicas = new LinkedHashMap<>();

        _eurekaServerContext.getPeerEurekaNodes()
                .getPeerNodesView()
                .forEach(peerEurekaNode -> {
                    try {
                        URI peerEureakNodeUri = new URI(peerEurekaNode.getServiceUrl());
                        replicas.put(peerEureakNodeUri.getHost(), scrubBasicAuth(peerEurekaNode.getServiceUrl()));
                    } catch (URISyntaxException ignored) {

                    }
                });
        context.put("replicas", replicas.entrySet());
        return context;
    }

    private void initInstanceInfo(Map<String, Object> context, StatusInfo statusInfo) {
        InstanceInfo instanceInfo = statusInfo.getInstanceInfo();
        Map<String, String> instanceMap = new HashMap<>();
        instanceMap.put("ipAddr", instanceInfo.getIPAddr());
        instanceMap.put("status", instanceInfo.getStatus().toString());
        if (instanceInfo.getDataCenterInfo().getName() == DataCenterInfo.Name.Amazon) {
            AmazonInfo info = (AmazonInfo) instanceInfo.getDataCenterInfo();
            instanceMap.put("availability-zone",
                    info.get(AmazonInfo.MetaDataKey.availabilityZone));
            instanceMap.put("public-ipv4", info.get(AmazonInfo.MetaDataKey.publicIpv4));
            instanceMap.put("instance-id", info.get(AmazonInfo.MetaDataKey.instanceId));
            instanceMap.put("public-hostname",
                    info.get(AmazonInfo.MetaDataKey.publicHostname));
            instanceMap.put("ami-id", info.get(AmazonInfo.MetaDataKey.amiId));
            instanceMap.put("instance-type",
                    info.get(AmazonInfo.MetaDataKey.instanceType));
        }
        context.put("instanceInfo", toPairs(instanceMap));
    }

    protected void filterReplicas(Map<String, Object> context, StatusInfo statusInfo) {
        Map<String, String> applicationStats = statusInfo.getApplicationStats();
        if (null == applicationStats) {
            context.put("applicationStats", new HashMap<>());
            return;
        }

        if(applicationStats.get("registered-replicas").contains("@")){
            applicationStats.put("registered-replicas", scrubBasicAuth(applicationStats.get("registered-replicas")));
        }
        if(applicationStats.get("unavailable-replicas").contains("@")){
            applicationStats.put("unavailable-replicas",scrubBasicAuth(applicationStats.get("unavailable-replicas")));
        }
        if(applicationStats.get("available-replicas").contains("@")){
            applicationStats.put("available-replicas",scrubBasicAuth(applicationStats.get("available-replicas")));
        }
        context.put("applicationStats", toPairs(applicationStats));
    }

    private void initApps(Map<String, Object> model) {
        List<Application> sortedApplications = getRegistry().getSortedApplications();
        ArrayList<Map<String, Object>> apps = new ArrayList<>();
        for (Application app : sortedApplications) {
            LinkedHashMap<String, Object> appData = new LinkedHashMap<>();
            apps.add(appData);
            appData.put("name", app.getName());
            Map<String, Integer> amiCounts = new HashMap<>();
            Map<InstanceInfo.InstanceStatus, List<Pair<String, String>>> instancesByStatus = new HashMap<>();
            Map<String, Integer> zoneCounts = new HashMap<>();
            for (InstanceInfo info : app.getInstances()) {
                String id = info.getId();
                String url = info.getStatusPageUrl();
                InstanceInfo.InstanceStatus status = info.getStatus();
                String ami = "n/a";
                String zone = "";
                if (info.getDataCenterInfo().getName() == DataCenterInfo.Name.Amazon) {
                    AmazonInfo dcInfo = (AmazonInfo) info.getDataCenterInfo();
                    ami = dcInfo.get(AmazonInfo.MetaDataKey.amiId);
                    zone = dcInfo.get(AmazonInfo.MetaDataKey.availabilityZone);
                }
                Integer count = amiCounts.get(ami);
                if (count != null) {
                    amiCounts.put(ami, count + 1);
                }
                else {
                    amiCounts.put(ami, 1);
                }
                count = zoneCounts.get(zone);
                if (count != null) {
                    zoneCounts.put(zone, count + 1);
                }
                else {
                    zoneCounts.put(zone, 1);
                }
                List<Pair<String, String>> list = instancesByStatus.computeIfAbsent(status, k -> new ArrayList<>());
                list.add(new Pair<>(id, url));
            }
            appData.put("amiCounts", amiCounts.entrySet());
            appData.put("zoneCounts", zoneCounts.entrySet());
            ArrayList<Map<String, Object>> instanceInfos = new ArrayList<>();
            appData.put("instanceInfos", instanceInfos);
            for (Map.Entry<InstanceInfo.InstanceStatus, List<Pair<String, String>>> entry : instancesByStatus
                    .entrySet()) {
                List<Pair<String, String>> value = entry.getValue();
                InstanceInfo.InstanceStatus status = entry.getKey();
                LinkedHashMap<String, Object> instanceData = new LinkedHashMap<>();
                instanceInfos.add(instanceData);
                instanceData.put("status", entry.getKey());
                ArrayList<Map<String, Object>> instances = new ArrayList<>();
                instanceData.put("instances", instances);
                instanceData.put("isNotUp", status != InstanceInfo.InstanceStatus.UP);

                // TODO

				/*
                 * if(status != InstanceInfo.InstanceStatus.UP){
				 * buf.append("<font color=red size=+1><b>"); }
				 * buf.append("<b>").append(status
				 * .name()).append("</b> (").append(value.size()).append(") - ");
				 * if(status != InstanceInfo.InstanceStatus.UP){
				 * buf.append("</font></b>"); }
				 */

                for (Pair<String, String> p : value) {
                    LinkedHashMap<String, Object> instance = new LinkedHashMap<>();
                    instances.add(instance);
                    instance.put("id", p.first());
                    String url = p.second();
                    instance.put("url", url);
                    boolean isHref = url != null && url.startsWith("http");
                    instance.put("isHref", isHref);
					/*
					 * String id = p.first(); String url = p.second(); if(url != null &&
					 * url.startsWith("http")){
					 * buf.append("<a href=\"").append(url).append("\">"); }else { url =
					 * null; } buf.append(id); if(url != null){ buf.append("</a>"); }
					 * buf.append(", ");
					 */
                }
            }
            // out.println("<td>" + buf.toString() + "</td></tr>");
        }
        model.put("apps", apps);
    }

    private <KEY,VALUE> Collection<Pair<KEY, VALUE>> toPairs(Map<KEY, VALUE> map) {
        return Collections.unmodifiableList(map
                .entrySet()
                .stream()
                .map((entrySet) -> new Pair<>(entrySet.getKey(), entrySet.getValue()))
                .collect(Collectors.toList()));
    }

    private String scrubBasicAuth(String urlList){
        String[] urls=urlList.split(",");
        String filteredUrls="";
        for(String u : urls){
            if(u.contains("@")){
                filteredUrls+=u.substring(0,u.indexOf("//")+2)+u.substring(u.indexOf("@")+1,u.length())+",";
            }else{
                filteredUrls+=u+",";
            }
        }
        return filteredUrls.substring(0,filteredUrls.length()-1);
    }
}
