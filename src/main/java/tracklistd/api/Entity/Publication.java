package tracklistd.api.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_publication", discriminatorType = DiscriminatorType.STRING)
public abstract class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    protected User author;

    @CreationTimestamp
    @Column(name = "publication_date", nullable = false, updatable = false)
    private LocalDateTime publicationDate;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "publication")
    private List<Like> likes = new ArrayList<>();

    public Publication() {
    }

    public Publication(User author) {
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public User getAuthorPublication() {
        return author;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
