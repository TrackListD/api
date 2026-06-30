package tracklistd.api.Entity;

import jakarta.persistence.*;
import lombok.Getter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import tracklistd.api.Entity.Enums.Privacy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@DiscriminatorColumn(name = "type_publication", discriminatorType = DiscriminatorType.STRING)
public abstract class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    protected User author;

    @Enumerated(EnumType.STRING)
    @Column(name = "who_can_see", nullable = false)
    private Privacy whoCanSee = Privacy.PUBLIC;

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
        this.whoCanSee = Privacy.PUBLIC;
    }

    public Publication(User author, Privacy whoCanSee) {
        this.author = author;
        this.whoCanSee = whoCanSee;
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

    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Privacy getWhoCanSee() {
        return whoCanSee;
    }

    public void setWhoCanSee(Privacy whoCanSee) {
        this.whoCanSee = whoCanSee;
    }

    public List<Like> getLikes() {
        return likes;
    }
}
