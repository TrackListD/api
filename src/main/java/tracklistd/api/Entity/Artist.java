package tracklistd.api.Entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "artists")
@NoArgsConstructor
public class Artist {

    @Id
    private Long spotifyID;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_picture_url")
    private String profilePictureURL;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "artist_genres", joinColumns = @JoinColumn(name = "artist_id"))
    @Column(name = "genre")
    private List<String> musicalGenres;

    @ManyToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Media> releasedMedia;
}
