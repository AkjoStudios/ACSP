package com.akjostudios.acsp.common.api;

import reactor.core.publisher.Mono;

public interface TokenProvider {
    Mono<String> getToken();
}