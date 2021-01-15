package com.javabom.bomjava;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ContextTest {

    @Test
    void monoContextTest1() {
        String key = "message";
        Mono<String> r = Mono.just("Hello")
                .flatMap(s ->
                        Mono.deferContextual(ctx -> Mono.just(s + " " + ctx.get(key))
                        )
                )
                .contextWrite(ctx -> ctx.put(key, "World"));

        StepVerifier.create(r)
                .expectNext("Hello World")
                .verifyComplete();
    }
}
