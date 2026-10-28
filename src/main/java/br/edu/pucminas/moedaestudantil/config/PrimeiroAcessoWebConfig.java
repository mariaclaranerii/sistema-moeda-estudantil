package br.edu.pucminas.moedaestudantil.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PrimeiroAcessoWebConfig implements WebMvcConfigurer {
    private final PrimeiroAcessoInterceptor interceptor;
    public PrimeiroAcessoWebConfig(PrimeiroAcessoInterceptor interceptor) {
        this.interceptor = interceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }
}
