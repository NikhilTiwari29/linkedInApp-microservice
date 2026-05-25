package com.nikhil.linkedin.api_gateway.filters;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter correlationIdFilter = new CorrelationIdFilter();

    @Test
    void generatesCorrelationIdWhenMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(correlationIdFilter.filter(exchange, chain -> {
                    String id = chain.getRequest().getHeaders().getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
                    assertNotNull(id);
                    return Mono.empty();
                }))
                .verifyComplete();
    }

    @Test
    void preservesExistingCorrelationId() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api")
                .header(CorrelationIdFilter.CORRELATION_ID_HEADER, "existing-id")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(correlationIdFilter.filter(exchange, chain -> {
                    assertEquals("existing-id",
                            chain.getRequest().getHeaders().getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER));
                    return Mono.empty();
                }))
                .verifyComplete();
    }
}
