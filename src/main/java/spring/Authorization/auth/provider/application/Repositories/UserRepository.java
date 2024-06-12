package spring.Authorization.auth.provider.application.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.Authorization.auth.provider.application.Entities.User;

import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByEmail(String email);

}