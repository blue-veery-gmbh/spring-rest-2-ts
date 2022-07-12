package com.blueveery.springrest2ts.webflux;


import com.blueveery.springrest2ts.Rest2tsGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebFluxConfigurator {
    public static void configure(Rest2tsGenerator tsGenerator) {
        tsGenerator.getCustomTypeMappingActions().put(Mono.class, new MonoMappingAction());
        tsGenerator.getCustomTypeMappingActions().put(Flux.class, new FluxMappingAction());
    }
}