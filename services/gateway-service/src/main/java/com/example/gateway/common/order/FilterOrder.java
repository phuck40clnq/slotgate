package com.example.gateway.common.order;

import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

/**
 * {@code FilterOrder} defines execution ordering
 * for gateway filters.
 * 
 * <p>
 * Orders that depend on security state
 * {@link org.springframework.security.config.web.server.SecurityWebFiltersOrder}
 * to ensure correct execution relative to
 * Spring Security's filter chain.
 * </p>
 * 
 * <p>
 * Rules:
 * <ul>
 *   <li>Infrastructure filters (trace, metrics) execute early</li>
 *   <li>Security-dependent filters execute after authentication</li>
 *   <li>Post-processing filters (e.g. logging) execute last</li>
 * </ul>
 * </p>
 */
public enum FilterOrder {

    /** 
     * Progagates authenticated identity information
     * after Spring Security authentication has completed.
     */
    IDENTITY_PROPAGATION(
        SecurityWebFiltersOrder.AUTHENTICATION.getOrder() + 10
    ),
    
    RATE_LIMIT(10),

    /**
     * Final logging filter executed at
     * the end of the filter chain.
     */
    LOGGING(
        SecurityWebFiltersOrder.LAST.getOrder()
    );

    private final int value;

    FilterOrder(int value) {
        this.value = value;
    }

    /**
     * Returns the filter execution order.
     * 
     * @return filter order value
     */
    public int value() { return this.value; }

}
