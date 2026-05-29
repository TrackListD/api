package tracklistd.api.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Entity.Interfaces.Reportable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_users")
@Getter
@Setter
@NoArgsConstructor
public class User implements Reportable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "quem_pode_comentar", nullable = false)
    private Privacy quemPodeComentar;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, updatable = true)
    private ModerationStatus moderationStatus = ModerationStatus.ACTIVE;

    @Override
    public String getContentReported() {
        return nome;
    }

    @Override
    public ModerationStatus getStatusModeration() {
        return moderationStatus;
    }

    @Override
    public Reportable getTarget() {
        return this;
    }
}