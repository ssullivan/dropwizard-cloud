package com.github.ssullivan.guice;

import com.google.inject.servlet.ServletModule;
import com.netflix.eureka.GzipEncodingEnforcingFilter;
import com.netflix.eureka.ServerRequestAuthFilter;
import com.netflix.eureka.StatusFilter;

/**
 * Created by catal on 3/24/2017.
 */
public class EurekaServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        filter("/*").through(StatusFilter.class);
        filter("/*").through(ServerRequestAuthFilter.class);
        filter("/v2/apps", "/v2/apps/*").through(GzipEncodingEnforcingFilter.class);
    }
}
