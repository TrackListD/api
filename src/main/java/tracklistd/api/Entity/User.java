package tracklistd.api.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_users")
@Getter
@Setter
@NoArgsConstructor

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "quem_pode_comentar", nullable = false)
    private Privacy quemPodeComentar;

    @Column(nullable = false, length = 100)
    private String nome;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDate dataCriacao;

    @Column(name = "data_expiracao")
    private LocalDate dataExpiracao;

    @Column(name = "bio", length = 500)
    private String bio;

    @ManyToMany
    @JoinTable(
            name = "user_seguindo",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    private List<User> seguindo;

    @ManyToMany(mappedBy = "seguindo")
    private List<User> seguidores;

    private String idLoginApi;

    private Boolean estaAtivo;

}
