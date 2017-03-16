package com.github.ssullivan.eureka;

/**
 * Created by catal on 3/14/2017.
 */
public final class EurekaConstants {
    private EurekaConstants() {
        throw new AssertionError("Do not instantiate constant utility class");
    }

    public static final String EUREKA_REGION = "eureka.region";
    public static final String EUREKA_SHOULD_FETCH_REGISTRY = "eureka.shouldFetchRegistry";
}
