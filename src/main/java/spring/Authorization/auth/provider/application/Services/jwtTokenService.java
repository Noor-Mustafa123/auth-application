package spring.Authorization.auth.provider.application.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service

public class jwtTokenService {

    // Use a secret key with at least 256 bits (32 characters if base64 encoded)
    private final String secretKey = "B5r5aTyLW1vdorJLHs9egCQB3NFgKm9p3kW5qTxMSp9RzU2s"; // Ensure this key is at least 32 bytes long

    private long expiration = 86400000;
    private long refreshTokenExpiration = 604800000;


   //simple Token method
   public String generateToken(UserDetails userDetails){
       return generatedToken(userDetails);
   }


    public String generatedToken(UserDetails userDetails){
       Map<String, Object> claimsMap = new HashMap<String, Object>();
       claimsMap.put("role" ,userDetails.getAuthorities());
      return buildToken(claimsMap, userDetails, expiration);
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        claimsMap.put("role" ,userDetails.getAuthorities());
        return buildToken(claimsMap, userDetails, refreshTokenExpiration);
    }




//this method will build the token
    public String buildToken(Map<String, Object>claims ,UserDetails userDetails,long expiration) {
        return Jwts
                .builder()
//                this sets the subject claim in the token
                .setSubject(userDetails.getUsername())
//                this sets any additional claim in the token
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ expiration) )
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
    public Claims getAllClaims(String token){
       return Jwts
               .parserBuilder()
               .setSigningKey(getSignInKey())
               .build()
//               The parseClaimsJws(String compact) method is used to parse and validate signed JWTs (JWS). It will validate the signature and throw a SignatureException if the signature is invalid.  On the other hand, the parseClaimsJwt(String compact) method is used to parse JWTs that are not signed (also known as Unsecured JWS). It does not validate the signature.
               .parseClaimsJws(token)
               .getBody();

    }


}


//it will create tokens for the authentication jwt tokens
//i need to create a method that builds token
// then i will need to create tow methods one will create a refreshtoken and the other will create a simpletoken


