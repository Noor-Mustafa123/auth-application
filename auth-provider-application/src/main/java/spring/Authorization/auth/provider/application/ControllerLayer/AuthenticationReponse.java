package spring.Authorization.auth.provider.application.ControllerLayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Component
public class AuthenticationReponse {

    @JsonProperty("access_token")
    private String jwtToken;
    @JsonProperty("refresh_token")
    private String refreshToken;

}
