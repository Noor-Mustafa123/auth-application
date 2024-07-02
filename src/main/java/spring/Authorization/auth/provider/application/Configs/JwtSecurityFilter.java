package spring.Authorization.auth.provider.application.Configs;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.Authorization.auth.provider.application.Entities.TokenEntity;
import spring.Authorization.auth.provider.application.Entities.User;
import spring.Authorization.auth.provider.application.Services.JwtTokenService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Configuration
public class JwtSecurityFilter extends OncePerRequestFilter {
    @Autowired
    JwtTokenService jwtService;
    @Autowired
    UserDetailsService userDetailsService;

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
    @Autowired
    private AntPathMatcher pathMatcher;

    @Override
//    TODO: -->
//   * the transactional annotation wont work because it only works on spring managed beans and this filter is not so i need to move the transactional functionality into a service layer
//   ? Check the error comment on the userRepo method call in the method to see the reason for this annotation

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//    TODO: check if the token is present and is of bearer type
//    TODO: get the subject email or username and match it with the database




        final var jwtToken = request.getHeader("Authorization");
////     check for the token presence and type

        if (jwtToken == null || !jwtToken.contains("Bearer")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized-Missing jwt Token");
            System.out.println("the program is hitting here");
            return;
        }

//      get the email from the body of the jwt token and check it in the database
        String jwt = jwtToken.substring(7);
        Claims claims = jwtService.getAllClaims(jwt);
        String email = jwtService.extractUsername(jwt);
        User user = jwtService.getUserFromDatabase(jwt);


//        CHECK WITH TIME IF THE TOKEN IS EXPIRED
        if(jwtService.isTokenExpired(claims)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized-Missing jwt Token");
            System.out.println("the token exp is overdue and expired");
            return;
        }

//  ! Here, you're trying to access the tokens collection of a User entity outside of a transactional context. When the user.getTokens() method is called, Hibernate tries to fetch the tokens collection from the database. However, because the Hibernate Session has already been closed (as the findByEmail method has completed), it can't fetch the collection, resulting in the LazyInitializationException.

        TokenEntity tokenObj = jwtService.getTokenObjFromUserObj(jwt);
        if(tokenObj==null){
            System.out.println("No matching/active token in the database");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("No matching/active token in the database");
            return;
        }

        String token = tokenObj.getToken();
        System.out.println(token);
        System.out.println(jwt);


//  ! add a revoked or expired check to not get the expired token from the database



        if (Objects.equals(token, jwt)) {
            filterChain.doFilter(request, response);
            System.out.println("The token matched successfully");


//  In the context of your JwtAuthenticationFilter, you're dealing with authentication, which is a concern of Spring Security. Therefore, you use UserDetailsService to integrate with Spring Security's authentication mechanism.

            if (SecurityContextHolder.getContext().getAuthentication() == null && email != null) {

                if (!tokenObj.isRevoked() && !tokenObj.isExpired()) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authenticationObj = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());
                    authenticationObj.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationObj);
                    System.out.println("added to security context");
                } else {
                    System.out.println("token is expired or revoked");
                }
            } else {
                System.out.println("the email is null or the application context is null");
            }
        }
       else {
            System.out.println("Token does not match");
        }
        filterChain.doFilter(request, response);
    }

//        Checks:
//        If the securitycontext is already null
//        if the list of the returned user objects and the tokens are expired or not ?
//        I will need to get the tokens separate

//        I will need the object of the authentication object to pass into the spring  security contexxt

//doing this because webConfiguration does not work on custom filters so we are overriding this method which makes the filter ignore specific paths
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
      String path = request.getServletPath();
      return Arrays.stream(WHITE_LIST_URL).anyMatch(pattern-> pathMatcher.match(pattern,path));
    }

}
