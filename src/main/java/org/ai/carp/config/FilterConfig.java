package org.ai.carp.config;

import org.ai.carp.filter.APILimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean registerAPILimitFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new APILimitFilter());
        registration.addUrlPatterns("/api/*");
        registration.setName("APILimitFilter");
        registration.setOrder(1);
        return registration;
    }

}
