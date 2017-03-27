package com.github.ssullivan.guice;

import com.google.inject.name.Named;
import com.google.inject.name.Names;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;

/**
 * Created by catal on 3/27/2017.
 */
public class EurekaServerModule extends DropwizardAwareModule {
    public static final String APPLICATION_CONTEXT_PATH = "applicationContextPath";

    @Override
    protected void configure() {


    }
}
