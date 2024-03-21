package com.akjostudios.acsp.backend.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
@Order(-2)
public class WebErrorHandler implements WebExceptionHandler {
    private final ObjectMapper objectMapper;

    @Override
    public @NotNull Mono<Void> handle(
            @NotNull ServerWebExchange exchange,
            @NotNull Throwable ex
    ) {
        ServerHttpRequest request = exchange.getRequest();

        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        if (ex instanceof ResponseStatusException responseStatusEx) {
            statusCode = responseStatusEx.getStatusCode().value();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                StringUtils.replaceChars(ex.getLocalizedMessage(), '"', '\''),
                statusCode,
                request.getURI().getPath()
        );
        log.error(
                "Error occurred while processing request to '{}' with status code {}: {}",
                errorResponse.path(), errorResponse.statusCode(), errorResponse.message()
        );

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