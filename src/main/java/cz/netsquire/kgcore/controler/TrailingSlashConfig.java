package cz.netsquire.kgcore.controler;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.UrlHandlerFilter;

@Configuration
public class TrailingSlashConfig {

    @Bean
    public FilterRegistrationBean<UrlHandlerFilter> trailingSlashRedirectFilter() {
        UrlHandlerFilter filter = UrlHandlerFilter
                .trailingSlashHandler("/**")           // all paths
                .redirect(HttpStatus.PERMANENT_REDIRECT) // 308 – best for SEO & caching
                //.redirect(HttpStatus.MOVED_PERMANENTLY) // 301 – also ok
                //.forward()                             // silent forward (less common for REST)
                .build();

        FilterRegistrationBean<UrlHandlerFilter> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setOrder(-100); // early in chain

        return registrationBean;
    }
}