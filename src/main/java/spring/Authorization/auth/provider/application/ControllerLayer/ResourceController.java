package spring.Authorization.auth.provider.application.ControllerLayer;


import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/resource")
public class ResourceController {


    @PostMapping("/test")
    public void testMethod(){
        System.out.println("the test method is hit");
    }



}
