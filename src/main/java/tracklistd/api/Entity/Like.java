package tracklistd.api.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
public class Like {

    // id - chave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // referencia ao usuario que curtiu
    @ManyToOne
    private User user;

    // referencia a publicação curtida
    @ManyToOne
    private Publication publication;

    // referencia a data da publicação (criada automaticamente)
    @CreationTimestamp
    private LocalDateTime dateTime;
}
