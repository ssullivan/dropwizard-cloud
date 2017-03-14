package com.github.ssullivan.lifecycle;

import com.netflix.zuul.FilterFileManager;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.groovy.GroovyFileFilter;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * Created by catal on 3/13/2017.
 */
public class ZuulGroovyManager implements Managed {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZuulGroovyManager.class);

    // This is the path that we will monitor for groovy based zuul filters
    private String _scriptRoot = "";


    public ZuulGroovyManager() {
    }

    public ZuulGroovyManager(@Nonnull String scriptRoot) {
        this._scriptRoot = scriptRoot;
    }


    public void start() throws Exception {
        LOGGER.info("ZuulGroovyManager start");
        FilterLoader.getInstance()
                .setCompiler(new GroovyCompiler());

        String scriptRoot = _scriptRoot == null || _scriptRoot.isEmpty() ? System.getProperty("zuul.filter.root", "") : "";
        if (scriptRoot.length() > 0) scriptRoot = scriptRoot + File.separator;
        try {
            LOGGER.info("Attempting to register GroovyFilterFilter for root {} and directories pre, route, and filters.post", scriptRoot);
            FilterFileManager.setFilenameFilter(new GroovyFileFilter());
            FilterFileManager.init(5, scriptRoot + "pre",
                    scriptRoot + "route",
                    scriptRoot + "post");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() throws Exception {
        LOGGER.info("ZuulGroovyManager stop");
    }
}
