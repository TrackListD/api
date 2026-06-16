package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import tracklistd.api.Entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByIdLoginApi(String idLoginApi);

    boolean existsByIdLoginApi(String idLoginApi);
}