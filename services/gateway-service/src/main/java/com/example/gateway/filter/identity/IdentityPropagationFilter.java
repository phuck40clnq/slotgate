package com.example.gateway.filter.identity;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.gateway.common.constants.HeaderConstants;
import com.example.gateway.common.order.FilterOrder;
import com.example.gateway.error.GatewayBaseException;
import com.example.gateway.error.GatewayErrorCode;

import reactor.core.publisher.Mono;

/**
 * Global filter responsible for propagating authenticated user
 * identity from Spring Security into downstream services.
 * 
 * <p>
 * - This filter <i>does NOT</i> <b>perform 
 * authentication</b> or <b>token verification</b>.
 * - Authentication is handled entirely by Spring Security
 * (OAuth2 Resource Server + JwtDecoder).
 * </p>
 * 
 * <p>
 * <h3>Responsibilities:</h3>
 * <ul>
 *   <li>
 *     Read the authenticated {@link java.security.Principal}
 *     from {@link org.springframework.web.server.ServerWebExchange}
 *   </li>
 *   <li>
 *     Ensure the request is authenticated
 *     If route is public, it will skip and return rightaway
 *   </li>
 *   <li>
 *     Extract identity information from the authentication token
 *   </li>
 *   <li>
 *     Propagate identity to downstream services via HTTP headers
 *   </li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Design principles</b>:
 * <ul>
 *   <li>
 *     Gateway does not parse or verify JWTs
 *   </li>
 *   <li>
 *     Gateway does not depend on Spring Security internals
 *   </li>
 *   <li>
 *     Gateway remains stateless and thin
 *   </li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Expected execution order</b>:
 * <ul>
 *   <li>
 *     Runs after {@link org.springframework.security.web.server.SecurityWebFilterChain}
 *   </li>
 *   <li>
 *     Assumes authentication has already been completed
 *   </li>
 * </ul>
 * </p>
 * 
 * <p>
 * 
 * <p>
 * <b>Downstream contract</b>:
 * <ul>
 *   <li>
 *     {@link com.example.gateway.common.constants.HeaderConstants#X_User_Id}
 *     {@code HeaderConstants.X_User_Id} header contains
 *     the authenticated user identifier
 *   </li>
 * </ul>
 * </p>
 */
@Component
public class IdentityPropagationFilter 
        implements GlobalFilter, Ordered{

    private final IdentitySkipRouteMatcher skipRouteMatcher;

    public IdentityPropagationFilter(IdentitySkipRouteMatcher skipRouteMatcher) {
        this.skipRouteMatcher = skipRouteMatcher;
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (skipRouteMatcher.shouldSkip(exchange)) {
            return chain.filter(exchange);
        }

        return exchange.getPrincipal()
            .switchIfEmpty(Mono.error(
                new GatewayBaseException(GatewayErrorCode.UNAUTHORIZED)
            ))
            .flatMap(principal -> {
                if (!(principal instanceof JwtAuthenticationToken jwtAuth)) {
                    return Mono.error(
                        new GatewayBaseException(GatewayErrorCode.UNAUTHORIZED)
                    );
                }

                String userId = jwtAuth.getToken().getSubject();

                ServerWebExchange mutatedExchange =
                        exchange.mutate()
                                .request(r -> {
                                    r.header(HeaderConstants.X_INTERNAL_User_Id, userId);
                                })
                                .build();
                
                return chain.filter(mutatedExchange);
            });
    }

    @Override
    public int getOrder() {
        /**
         * Run after SecurityWebFilterChain
         * Return the value gt SecurityWebFilterChain (-100)
         */
        return FilterOrder.IDENTITY_PROPAGATION.value();
    }

}
