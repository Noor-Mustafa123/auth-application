package spring.Authorization.auth.provider.application.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenEntity {

    @Id
    @GeneratedValue
    public Integer id;
//    this annotation means that tthere should be no duplicate values in the database
    @Column(unique = true)
    public String refreshToken;

    @Column(unique = true)
    public String token;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;

    public boolean revoked;

    public boolean expired;

    // make a one ot many relationship
    @ManyToOne(fetch = FetchType.LAZY)
    public User user;


}

