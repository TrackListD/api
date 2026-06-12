package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tracklistd.api.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByIdLoginApi(String idLoginApi);
}