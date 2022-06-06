package woowacourse.auth.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import woowacourse.auth.ui.AuthInterceptor;
import woowacourse.auth.ui.AuthenticationPrincipalArgumentResolver;

@Configuration
public class AuthenticationPrincipalConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver;

    public AuthenticationPrincipalConfig(AuthInterceptor authInterceptor,
                                         AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver) {
        this.authInterceptor = authInterceptor;
        this.authenticationPrincipalArgumentResolver = authenticationPrincipalArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/members/**")
                .addPathPatterns("/api/carts/**")
                .excludePathPatterns("/api/members", "/api/members/email-check");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(authenticationPrincipalArgumentResolver);
    }
}
