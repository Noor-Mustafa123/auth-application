package spring.Authorization.auth.provider.application.Configs;


import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.Authorization.auth.provider.application.Entities.User;
import spring.Authorization.auth.provider.application.Repositories.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Configuration

public class ApplicationConfiguration {


    private final UserRepository repository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            List<User> users = repository.findByEmail(username);
            if (users.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }
            return users.get(0);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

}

