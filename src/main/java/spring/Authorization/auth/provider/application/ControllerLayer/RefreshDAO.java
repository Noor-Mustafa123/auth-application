package spring.Authorization.auth.provider.application.ControllerLayer;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RefreshDAO {
    String refreshToken;
}
