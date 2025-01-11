package ru.newvasuki.smarthome.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    private static final String[] REACT_STATIC = new String[] { "/index*", "/static/**", "/*.js", "/*.json", "/*.ico", "/app/auth", "/login" };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.GET, REACT_STATIC).permitAll()
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login").loginProcessingUrl("/perform_login").defaultSuccessUrl("/",true);
        return http.build();
    }
}
