package spring.Authorization.auth.provider.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import spring.Authorization.auth.provider.application.Services.AuthenticationService;

@SpringBootApplication
public class AuthProviderApplication {



	public static void main(String[] args) {
		SpringApplication.run(AuthProviderApplication.class, args);
	}

}


//TODO: To make entities like token and save the user registration detials to the database
//TODO: make hte subsequent relationship in the database that there is a ONETOMANY relationship between teh user and the tokens
