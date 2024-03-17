package com.akjostudios.acsp.backend.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@RequiredArgsConstructor
@Order(-2)
public class ErrorHandler implements WebExceptionHandler {
    private final ObjectMapper objectMapper;

    @Override
    public @NotNull Mono<Void> handle(
            @NotNull ServerWebExchange exchange,
            @NotNull Throwable ex
    ) {
        ServerHttpRequest request = exchange.getRequest();
        ErrorResponse errorResponse = new ErrorResponse(
                StringUtils.replaceChars(ex.getLocalizedMessage(), '"', '\''),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getURI().getPath()
        );

        if (ex instanceof ResponseStatusException responseStatusEx) {
            errorResponse = new ErrorResponse(
                    StringUtils.replaceChars(responseStatusEx.getLocalizedMessage(), '"', '\''),
                    responseStatusEx.getStatusCode().value(),
                    request.getURI().getPath()
            );
        }

        try {
            return exchange.getResponse().writeWith(Mono.just(
                    exchange.getResponse().bufferFactory().wrap(
                            objectMapper.writeValueAsBytes(errorResponse)
                    )
            ));
        } catch (Exception e) { return Mono.error(e); }
    }

    public record ErrorResponse(
            @NotNull String message,
            int statusCode,
            @NotNull String path
    ) {}
}