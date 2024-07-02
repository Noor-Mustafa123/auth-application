package spring.Authorization.auth.provider.application.Configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import spring.Authorization.auth.provider.application.Configs.JwtSecurityFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static spring.Authorization.auth.provider.application.Entities.Permission.*;
import static spring.Authorization.auth.provider.application.Entities.Role.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    private final JwtSecurityFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

// ? FLUENT API is used for configuration which looks like the builder object creational pattern
        http
//  because we are not using cookies for session management then we are dont need the csrf property active so we disable it
                .csrf(AbstractHttpConfigurer::disable)
//  this is used to configure authorization rules for HTTP requests in a Spring Security application. This method allows you to specify which requests should be authorized based on roles, permissions, or other criteria.
//   The AuthorizedHttpRequestsConfigurer is a class provided by Spring Security. When you configure it, you are indeed configuring Spring Security.
//authorizeHttpRequests Method: This method is part of the HttpSecurity class in Spring Security. It allows you to configure authorization rules for HTTP requests.
                .authorizeHttpRequests((httpRequest) -> {
                    httpRequest.requestMatchers(WHITE_LIST_URL).permitAll()
                            .requestMatchers("/api/v1/auth/register").permitAll()//Explicitly allowing the register endpoint
                            .requestMatchers("/api/v1/auth/login").permitAll()//Explicitly allowing the login endpoint
                            .requestMatchers("/api/v1/auth/authenticate").permitAll()//explicitly allowing the authenticate plugin for testing purposes
// This means that any user with the ADMIN or MANAGER role can access any endpoint under /api/v1/management/**.
                            .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
// Even if a user has the ADMIN or MANAGER role, they must also have the specific permission (authority) to perform the action (GET, POST, PUT, DELETE) on the /api/v1/management/** endpoint.
                            .requestMatchers(GET, "/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                            .requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                            .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                            .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                            .anyRequest().authenticated();
// MY UNDERSTANDING : the method authorizeHttpREquests method takes in an interface Customizer as  a parameter and the customizer interface has an abstract method customize and I have implemented that method as a lambda expression inside the authorizeHttpREquest parameters and then inside the body fo the lambda expression method I have configured the AuhthorizedHttpREquestConfigurer class object and further configured that ojbec using the fluent API to allow certain API to alow certain endpoints
                })
// By setting it to STATELESS, you are indicating that the application should not use HTTP sessions to store security information. Instead, each request must be authenticated independently, which is typical for stateless applications using JWTs.
                .sessionManagement((sessionManConfigObj)->{
                    sessionManConfigObj.sessionCreationPolicy(STATELESS);
                })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                


//                http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(req ->
//                        req.requestMatchers(WHITE_LIST_URL)
//                                .permitAll()
//                                .requestMatchers("/api/v1/auth/register").permitAll() // Explicitly permit register endpoint
//                                .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
//                                .requestMatchers(GET, "/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
//                                .requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
//                                .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
//                                .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
//                                .anyRequest()
//                                .authenticated()
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .logout(logout ->
//                        logout.logoutUrl("/api/v1/auth/logout")
//                                .addLogoutHandler(logoutHandler)
//                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
//                );
//
        return http.build();
    }
}


//


//? the userDetailsService is an interface which is not editable it is used by spring to get the userDetials object meaning the uesr entity from the database
//? the relation of the userDetails with the AuthenticationManager is that the  AuthenticationManager delegates the authentication request to the conigured authenticationProvider and the AuthenticationProvider uses the UserDetailsService to get the object from the usrDetails from the database and return those details