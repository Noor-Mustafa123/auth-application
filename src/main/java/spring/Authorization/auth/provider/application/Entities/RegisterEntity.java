package spring.Authorization.auth.provider.application.Entities;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RegisterEntity  {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;
}
