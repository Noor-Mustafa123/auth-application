package spring.Authorization.auth.provider.application.Configs;


import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.AntPathMatcher;
import spring.Authorization.auth.provider.application.Entities.User;
import spring.Authorization.auth.provider.application.Repositories.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Configuration

public class ApplicationConfiguration {


    private final UserRepository repository;

    // The UserDetailsService is an interface provided by Spring Security that is used to retrieve user-related data. It is used by the DaoAuthenticationProvider to handle data access for user authentication. the userDetailsService return an object of the interface userDetails which is  the user
    // So we return the lambda expression itself? so whta is this lambda expression used as a lambda expression is used as an object of the userDetailsService ? is that correct

   // Purpose: This service is used to load user-specific data. It is a core interface in Spring Security for retrieving user information.
   // Bean Creation: By defining it as a bean, you allow Spring to inject it wherever needed, such as in the AuthenticationProvider.
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            List<User> userList = repository.findByEmail(username);
            return userList.getFirst();
        };
    }


    //  Purpose: This component is used to encode and verify passwords.
//  Bean Creation: Defining it as a bean allows Spring Security to use it for password encoding and matching.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //  Purpose: This component is responsible for authenticating a user by comparing the provided credentials with the stored user details.
//  Bean Creation: Defining it as a bean allows Spring Security to use it in the authentication process.
    @Bean
    public AuthenticationProvider authenticationProvider() {
         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AntPathMatcher antPathMatcher(){
        return new AntPathMatcher();
    }


    //   Purpose: This is the main interface for authentication in Spring Security. It delegates authentication requests to the configured AuthenticationProvider.
//   Bean Creation: By creating it as a bean, you can inject it into your services, such as the AuthenticationService, to perform authentication.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfig) throws Exception {
        return authenticationConfig.getAuthenticationManager();
    }

}

//TODO: Steps:-

//Security Configuration: Configure Spring Security to permit access to authentication endpoints.
//Authentication Controller: Create a controller to handle registration and authentication requests.
// *--Create a login entity that maps the user incoming login data
//Authentication Service: Implement the service to handle business logic for authentication.
//Application Configuration: Define beans for UserDetailsService, AuthenticationProvider, AuthenticationManager, and PasswordEncoder.
//By following these steps, you can incorporate the authentication functionality into your own Spring Boot project.

