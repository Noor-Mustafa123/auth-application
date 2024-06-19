package spring.Authorization.auth.provider.application.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "User")

public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Integer id;


    private String firstname;
    private String lastname;
    private String email;
    private String password;

    // the annotation is specific to the jpa for the persistence of the role data to the database
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
//    ? you can use the FetchMode.JOIN strategy to tell Hibernate to fetch the associated TokenEntity objects when it fetches a User object. This is known as eager fetching.
    @Fetch(FetchMode.JOIN)
    @ToString.Exclude
    private List<TokenEntity> tokens = new ArrayList<>();




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
