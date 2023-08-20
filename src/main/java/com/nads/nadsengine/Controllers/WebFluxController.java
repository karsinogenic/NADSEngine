package com.nads.nadsengine.Controllers;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
// import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
public class WebFluxController {

    @GetMapping(value = "/endpoint")
    public Mono<String> myEndpoint() {
        return Mono.just("Hello, world!");
    }
}
