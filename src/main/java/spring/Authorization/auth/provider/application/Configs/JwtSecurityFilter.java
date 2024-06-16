package spring.Authorization.auth.provider.application.Configs;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.Authorization.auth.provider.application.Entities.TokenEntity;
import spring.Authorization.auth.provider.application.Entities.User;
import spring.Authorization.auth.provider.application.Repositories.UserRepository;
import spring.Authorization.auth.provider.application.Services.jwtTokenService;

import java.io.IOException;
import java.util.List;

@Configuration
public class JwtSecurityFilter extends OncePerRequestFilter {
    @Autowired
    jwtTokenService jwtService;
    @Autowired
    UserRepository userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//    TODO: check if the token is present and is of bearer type
//    TODO: get the subject email or username and match it with the database

        final var jwtToken = request.getHeader("Authorization");
//     check for the token presence and type

//        if (jwtToken == null || !jwtToken.contains("Bearer")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

//      get the email from the body of the jwt token and check it in the database
        String jwt = jwtToken.substring(7);
        Claims claims = jwtService.getAllClaims(jwt);
        String email = claims.getSubject();
        System.out.println(email);
        System.out.println(claims);

        List<User> userList = userRepo.findByEmail(email);

        User user = userList.get(0);

        List<TokenEntity> listOfTokens = user.getTokens();

        TokenEntity tokenObj = listOfTokens.get(1);

        String token = tokenObj.getToken();

        if(token == jwt){
            filterChain.doFilter(request,response);
            System.out.println("The token matched sucessfully");
            return;
        }
        else{
            System.out.println("the token matching check failed");
        }

//  TODO:  create a method in the jwtService that extracts the subject form the jwttoken


// *this is not info
// ? this is informational
//  ! BUG HINTS that if i register with role USER it registers and returns key but if i register with ADMIN it returns unauthorized
//! also the data for the user of an admin is being saved to the database but no token is being saved for the ADMIN user when he registers
    }
}
