package com.github.ssullivan.view;

import com.fasterxml.jackson.databind.util.ViewMatcher;
import io.dropwizard.views.View;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by catal on 3/26/2017.
 */
public class EurekaStatusView extends View {
    private static final String TEMPLATE_NAME = "/templates/eureka/status.mustache";
    private Map<String, Object> _context;

    public EurekaStatusView(final Map<String, Object> context) {
        super(TEMPLATE_NAME, Charset.forName("UTF-8"));
        _context = context;
    }

    public Map<String, Object> getContext() {
        return _context;
    }
}
