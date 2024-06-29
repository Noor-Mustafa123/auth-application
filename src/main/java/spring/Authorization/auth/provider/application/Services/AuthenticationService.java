package spring.Authorization.auth.provider.application.Services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.Authorization.auth.provider.application.ControllerLayer.AuthenticationReponse;
import spring.Authorization.auth.provider.application.Entities.*;
import spring.Authorization.auth.provider.application.Repositories.TokenRepository;
import spring.Authorization.auth.provider.application.Repositories.UserRepository;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class AuthenticationService {


    private PasswordEncoder passwordEncoder;

    private jwtTokenService jwtService;

    private AuthenticationReponse authenticationReponse;

    private UserRepository userRepo;

    private TokenRepository tokenRepo;

    private AuthenticationManager authenticationManager;


    // method to extract data from the request and create user entity object
    // use that object to create a token
    // method to save user to the database
    // create an entity named token which will have a one-to-many relationship with user entity then save both to the datbase

    public AuthenticationReponse registerNewUser(RegisterEntity registerRequest) {
//        creating an object of the entity of the user

        var UserObj = new User();
        UserObj.setEmail(registerRequest.getEmail());
        UserObj.setFirstname(registerRequest.getFirstname());
        UserObj.setLastname(registerRequest.getLastname());
        UserObj.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        UserObj.setRole(registerRequest.getRole());


        var token = jwtService.generatedToken(UserObj);
        var refreshToken = jwtService.generateRefreshToken(UserObj);
        int length = refreshToken.length();
        System.out.println("The length of the string is: " + length);
        //        creating the object of the token entity to save it
        var TokenObj = TokenEntity.builder()
                .user(UserObj)
                .revoked(false)
                .expired(false)
                .token(token)
                .tokenType(TokenType.BEARER)
                .refreshToken(refreshToken)
                .build();


        UserObj.getTokens().add(TokenObj);
        userRepo.save(UserObj);

        authenticationReponse.setJwtToken(token);
        authenticationReponse.setRefreshToken(refreshToken);

        tokenRepo.save(TokenObj);

        return authenticationReponse;

    }


    // ! make a method that uses the authorization provider in order to check the user and authenticate it
// ? AuthenticationManager: This is the main entry point for the authentication process. When you call authenticationManager.authenticate(...), it delegates the actual authentication process to the configured AuthenticationProvider.
    public AuthenticationReponse authenticateUser(Login loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
//        get the users details from the database matching and generet tokens and return it to the user
        List<User> userList = userRepo.findByEmail(loginRequest.getEmail());
        User user = userList.getFirst();

       String jwtToken = jwtService.generateToken(user);
       String jwtRefreshToken =  jwtService.generateRefreshToken(user);

       return AuthenticationReponse.builder()
               .jwtToken(jwtToken)
               .refreshToken(jwtRefreshToken)
               .build();
    }


//    TODO: to make the token entity so that i can save the data to the database
//    TODO: to make method to save the data to the database
//    TODO: to make repository classes


}


