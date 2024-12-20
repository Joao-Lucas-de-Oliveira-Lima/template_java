package com.templete.back_end.Security;

import com.templete.back_end.Repository.UsuarioRepository;
import com.templete.back_end.Services.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
    private final TokenService tokenService;
    private final AuthorizationService authorizationService;

    public SecurityConfigurations(TokenService tokenService, AuthorizationService authorizationService) {
        this.tokenService = tokenService;
        this.authorizationService = authorizationService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    SecurityFilter securityFilter = new SecurityFilter(tokenService, authorizationService);
     return httpSecurity
             .cors(cors -> {})
             .httpBasic(AbstractHttpConfigurer::disable)
             .csrf(AbstractHttpConfigurer::disable)
             .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
             .authorizeHttpRequests(authorize -> authorize
                     .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                     .requestMatchers(HttpMethod.POST, "/api/register").permitAll()
                     .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                     .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                     .anyRequest().authenticated()
             ).build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
