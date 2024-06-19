package spring.Authorization.auth.provider.application.Configs;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.Authorization.auth.provider.application.Entities.TokenEntity;
import spring.Authorization.auth.provider.application.Entities.User;
import spring.Authorization.auth.provider.application.Repositories.UserRepository;
import spring.Authorization.auth.provider.application.Services.jwtTokenService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Configuration
public class JwtSecurityFilter extends OncePerRequestFilter {
    @Autowired
    jwtTokenService jwtService;


    @Override
//    TODO: --> -->
//   * the transactional annotation wont work because it only works on spring managed beans and this filter is not so i need to move the transactional functionality into a service layer
//   ? Check the error comment on the userRepo method call in the method to see the reason for this annotation

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//    TODO: check if the token is present and is of bearer type
//    TODO: get the subject email or username and match it with the database

        final var jwtToken = request.getHeader("Authorization");
//     check for the token presence and type

        if (jwtToken == null || !jwtToken.contains("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

//      get the email from the body of the jwt token and check it in the database
        String jwt = jwtToken.substring(7);
        Claims claims = jwtService.getAllClaims(jwt);
        String email = jwtService.extractUsername(jwt);
        User user = jwtService.getUserFromDatabase(jwt);


//  ! Here, you're trying to access the tokens collection of a User entity outside of a transactional context. When the user.getTokens() method is called, Hibernate tries to fetch the tokens collection from the database. However, because the Hibernate Session has already been closed (as the findByEmail method has completed), it can't fetch the collection, resulting in the LazyInitializationException.


      TokenEntity tokenObj = jwtService.getTokenObjFromUserObj(jwt);
        String token = tokenObj.getToken();
        System.out.println(token);
        System.out.println(jwt);

        if(Objects.equals(token, jwt)){
            filterChain.doFilter(request,response);
            System.out.println("The token matched successfully");
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
