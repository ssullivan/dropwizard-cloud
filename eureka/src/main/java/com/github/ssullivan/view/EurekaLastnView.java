package com.github.ssullivan.view;

import io.dropwizard.views.View;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by catal on 3/27/2017.
 */
public class EurekaLastnView extends View {
    private static final String TEMPLATE_NAME = "/templates/eureka/lastn.mustache";
    private Map<String, Object> _context;

    public EurekaLastnView(final Map<String, Object> context) {
        super(TEMPLATE_NAME, Charset.forName("UTF-8"));
        _context = context;
    }

    public Map<String, Object> getContext() {
        return _context;
    }
}
