package com.nads.nadsengine.Components;

import java.time.Duration;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

public class WebFluxFilter implements WebFilter {

    @Override
    public Mono filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        return Mono
                .delay(Duration.ofMillis(200))
                .then(
                        webFilterChain.filter(serverWebExchange));
    }

}
