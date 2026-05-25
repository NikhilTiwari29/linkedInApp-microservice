package com.nikhil.linkedin.api_gateway.filters;

import com.nikhil.linkedin.api_gateway.JwtService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    private AuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        authenticationFilter = new AuthenticationFilter(jwtService);
    }

    @Test
    void rejectsWhenAuthorizationHeaderMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/posts/core").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        StepVerifier.create(filter.filter(exchange, chain -> Mono.empty()))
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void rejectsWhenTokenInvalid() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/posts/core")
                .header("Authorization", "Bearer invalid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        when(jwtService.getUserIdFromToken("invalid-token")).thenThrow(new JwtException("invalid"));

        StepVerifier.create(filter.filter(exchange, chain -> Mono.empty()))
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void rejectsMalformedBearerHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/posts/core")
                .header("Authorization", "Bearertoken-without-space")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        StepVerifier.create(filter.filter(exchange, chain -> Mono.empty()))
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void extractBearerToken_returnsNullForInvalidHeaders() {
        assertEquals(null, AuthenticationFilter.extractBearerToken(null));
        assertEquals(null, AuthenticationFilter.extractBearerToken("Basic abc"));
        assertEquals(null, AuthenticationFilter.extractBearerToken("Bearer "));
    }

    @Test
    void extractBearerToken_parsesValidHeader() {
        assertEquals("my-token", AuthenticationFilter.extractBearerToken("Bearer my-token"));
    }

    @Test
    void addsUserIdHeaderWhenTokenValid() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/posts/core")
                .header("Authorization", "Bearer valid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        when(jwtService.getUserIdFromToken("valid-token")).thenReturn("99");

        StepVerifier.create(filter.filter(exchange, chain -> {
                    assertEquals("99", chain.getRequest().getHeaders().getFirst("X-User-Id"));
                    return Mono.empty();
                }))
                .verifyComplete();
    }
}
