package tracklistd.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tracklistd.api.Entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // por enquanto nao adicionei nenhum metodo aqui irei adicionar de acordo com
}
