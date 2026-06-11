package tracklistd.api.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter // gerar getters
@Setter
@Table(name = "like_content", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "publication_id" })
})
@NoArgsConstructor
public class Like {

    // id - chave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // referencia ao usuario que curtiu
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // referencia a publicação curtida
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    // referencia a data da publicação (criada automaticamente)
    @Column(updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime dateTime;
}
