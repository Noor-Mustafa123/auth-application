package spring.Authorization.auth.provider.application.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.Authorization.auth.provider.application.ControllerLayer.AuthenticationReponse;
import spring.Authorization.auth.provider.application.Entities.RegisterEntity;
import spring.Authorization.auth.provider.application.Entities.TokenEntity;
import spring.Authorization.auth.provider.application.Entities.TokenType;
import spring.Authorization.auth.provider.application.Entities.User;
import spring.Authorization.auth.provider.application.Repositories.TokenRepository;
import spring.Authorization.auth.provider.application.Repositories.UserRepository;

@Service
public class AuthenticationService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private jwtTokenService jwtService;
    @Autowired
    private AuthenticationReponse authenticationReponse;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private TokenRepository tokenRepo;

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


//    TODO: to make the token entity so that i can save the data to the database
//    TODO: to make method to save the data to the database
//    TODO: to make repository classes


}


