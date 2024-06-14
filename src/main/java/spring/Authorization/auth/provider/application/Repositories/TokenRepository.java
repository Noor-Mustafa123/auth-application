package spring.Authorization.auth.provider.application.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.Authorization.auth.provider.application.Entities.TokenEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Integer> {

//    List<TokenEntity> findByToken(String token);
    Optional<TokenEntity> findByToken(String token);

}
