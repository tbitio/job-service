package com.easy.jobservice.configuration;

import com.easy.jobservice.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
@ComponentScan
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final TokenStore tokenStore;

    @Autowired
    public ResourceServerConfiguration(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/v1/admin/**").hasAuthority(Constants.ROLE_ADMIN_STRING)
                .antMatchers("/api/v1/public/**").permitAll()
                .antMatchers("/api/v1/**").hasAuthority(Constants.ROLE_USER_STRING);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(tokenStore);
    }
}
