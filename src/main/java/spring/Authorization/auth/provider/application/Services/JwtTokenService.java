package spring.Authorization.auth.provider.application.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import spring.Authorization.auth.provider.application.Entities.TokenEntity;
import spring.Authorization.auth.provider.application.Entities.User;
import spring.Authorization.auth.provider.application.Repositories.TokenRepository;
import spring.Authorization.auth.provider.application.Repositories.UserRepository;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@Service

public class JwtTokenService {

    // Use a secret key with at least 256 bits (32 characters if base64 encoded)
    private final String secretKey = "B5r5aTyLW1vdorJLHs9egCQB3NFgKm9p3kW5qTxMSp9RzU2s"; // Ensure this key is at least 32 bytes long

    private long expiration = 86400000;
    private long refreshTokenExpiration = 604800000;


    @Autowired
    UserRepository userRepo;
    @Autowired
    TokenRepository tokenRepo;

    //simple Token method
    public String generateToken(UserDetails userDetails) {
        return generatedToken(userDetails);
    }


    public String generatedToken(UserDetails userDetails) {
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        claimsMap.put("role", userDetails.getAuthorities());
        return buildToken(claimsMap, userDetails, expiration);
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        claimsMap.put("role", userDetails.getAuthorities());
        return buildToken(claimsMap, userDetails, refreshTokenExpiration);
    }


    //this method will build the token
    public String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
        System.out.println(userDetails.getUsername());
        return Jwts
//! The issue seems to be related to the order of method calls in your buildToken method. In the JWT builder, the setClaims method will replace any existing claims on the builder, including the subject that you set with setSubject
                .builder()
//                this sets any additional claim in the token
                .setClaims(claims)
//                this sets the subject claim in the token
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                a method that creates a signing key
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    //    we are using the secret key to create an instance of Key object which is then being used to sign the JWT
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    //create a method to get all claims from the token
    //create a method to get all claims from the token
    public Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
//               The parseClaimsJws(String compact) method is used to parse and validate signed JWTs (JWS). It will validate the signature and throw a SignatureException if the signature is invalid.  On the other hand, the parseClaimsJwt(String compact) method is used to parse JWTs that are not signed (also known as Unsecured JWS). It does not validate the signature.
                .parseClaimsJws(token)
                .getBody();

    }

    //this method is used to extract any specific claim by name
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
//    we are getting the object of the FunctionInterface we are using methods of that class
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
//    The Claims::getSubject syntax is a method reference, which is a shorthand syntax for a lambda expression. It's used to point to a method without executing it. When you use this method reference, Java will automatically provide the instance of Claims for you.
//    The claims is an interface so in order to get method from it we need to create a class first but here is where lambda expression comes in when using the
        return extractClaim(token, Claims::getSubject);
    }


    public User getUserFromDatabase(String jwt) {
        String email = extractUsername(jwt);
        System.out.println("Email extracted from JWT: " + email);
        List<User> userList = userRepo.findByEmail(email);
        System.out.println("Users found with email: " + userList);
        if (userList.isEmpty()) {
            return null;
        } else {
            User user = userList.get(0);
            return user;
        }
    }

    public TokenEntity getTokenObjFromUserObj(String jwt) {
        User userObj = getUserFromDatabase(jwt);
        List<TokenEntity> listOfTokens = userObj.getTokens();
        System.out.println(listOfTokens);

//        this is getting the token from the database

        listOfTokens.removeIf(token -> token.isExpired() || token.isRevoked());


        TokenEntity tokenObj = null;
       if(!listOfTokens.isEmpty()){
         tokenObj = listOfTokens.getFirst();
           }
        return tokenObj;
    }

    public boolean isTokenExpired(Claims claims){
      return claims.getExpiration().before(new Date());
    }



    public TokenEntity isTokenValid(String refreshToken) throws Exception{
        List<TokenEntity> tokenList = tokenRepo.findTokenEntitiesByRefreshToken(refreshToken);
        if (tokenList.isEmpty()) {
            throw new Exception("No token found"); // or throw new Exception("No token found");
        }
            return tokenList.get(0);

    }




// method to handle expiry of token


}


//it will create tokens for the authentication jwt tokens
//i need to create a method that builds token
// then i will need to create tow methods one will create a refreshtoken and the other will create a simpletoken


