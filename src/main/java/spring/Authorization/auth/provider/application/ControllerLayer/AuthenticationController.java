package spring.Authorization.auth.provider.application.ControllerLayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.Authorization.auth.provider.application.Entities.RegisterEntity;
import spring.Authorization.auth.provider.application.Services.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")

public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/register")
    public ResponseEntity<AuthenticationReponse> registerNewUser(@RequestBody RegisterEntity request) {
            return ResponseEntity.ok(authenticationService.registerNewUser(request));
    }


    @PostMapping("/authenticate")
//     i have made this change
    public void registerNewUser() {
        System.out.println("The controller has been reached");
    }
}
