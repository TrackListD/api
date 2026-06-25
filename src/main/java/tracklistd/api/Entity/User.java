package tracklistd.api.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Entity.Interfaces.Reportable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tl_users")
@Getter
@Setter
@NoArgsConstructor
public class User implements Reportable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "profile_pic", length = 250)
    private String profilePic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "who_can_comment", nullable = false)
    private Privacy whoCanComment;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "suspension_end_date")
    private LocalDateTime suspensionEndDate;

    @Column(name = "bio", length = 500)
    private String bio;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "following", joinColumns = @JoinColumn(name = "follower_id"), inverseJoinColumns = @JoinColumn(name = "followed_id"))
    private Set<User> following = new HashSet<>(); // sets para evitar duplicatas

    @JsonIgnore
    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    @Column(name = "login_id", length = 250, nullable = false, unique = true)
    private String idLoginApi;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, updatable = true)
    private ModerationStatus moderationStatus = ModerationStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "favorite_music")
    private Music favoriteMusic;

    @ManyToOne
    @JoinColumn(name = "favorite_album")
    private Album favoriteAlbum;

    @ManyToOne
    @JoinColumn(name = "favorite_artist")
    private Artist favoriteArtist;

    public User(String name, String idLoginApi, Role role, Privacy privacy) {
        this.name = name;
        this.idLoginApi = idLoginApi;
        this.role = role;
        this.whoCanComment = privacy;
        this.moderationStatus = ModerationStatus.ACTIVE;
    }

    @Override
    public String getContentReported() {
        return name;
    }

    @Override
    public ModerationStatus getStatusModeration() {
        return moderationStatus;
    }

    @JsonIgnore
    @Override
    public Reportable getTarget() {
        return this;
    }

}