package org.ai.carp.filter;

import es.moki.ratelimitj.core.limiter.request.RequestLimitRule;
import es.moki.ratelimitj.core.limiter.request.RequestRateLimiter;
import es.moki.ratelimitj.inmemory.request.InMemorySlidingWindowRequestRateLimiter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;

public class APILimitFilter implements Filter {

    Set<RequestLimitRule> rules = Collections.singleton(
            RequestLimitRule.of(Duration.of(1, ChronoUnit.MINUTES), 100));
    RequestRateLimiter requestRateLimiter = new InMemorySlidingWindowRequestRateLimiter(rules);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    private boolean reached(String key) {
        return requestRateLimiter.overLimitWhenIncremented(key);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (reached(request.getRemoteHost())) {
            ((HttpServletResponse) response).sendError(429, "Too many requests!");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
